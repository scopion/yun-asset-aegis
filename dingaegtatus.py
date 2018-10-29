#! /usr/bin/env python
# _*_ coding:utf-8 _*_

import requests
import json
import sys,datetime
import requests
import MySQLdb.cursors
import MySQLdb as mdb
import time
import re
#from json import JSONDecodeError
import logging
import requests


def is_not_null_and_blank_str(content):
    """
    非空字符串
    :param content: 字符串
    :return: 非空 - True，空 - False

    >>> is_not_null_and_blank_str('')
    False
    >>> is_not_null_and_blank_str(' ')
    False
    >>> is_not_null_and_blank_str('  ')
    False
    >>> is_not_null_and_blank_str('123')
    True
    """
    if content and content.strip():
        return True
    else:
        return False


class DingtalkChatbot(object):
    """
    钉钉群自定义机器人（每个机器人每分钟最多发送20条），支持文本（text）、连接（link）、markdown三种消息类型！
    """
    def __init__(self, webhook):
        """
        机器人初始化
        :param webhook: 钉钉群自定义机器人webhook地址
        """
        super(DingtalkChatbot, self).__init__()
        self.headers = {'Content-Type': 'application/json; charset=utf-8'}
        self.webhook = webhook
        self.times = 0
        self.start_time = time.time()

    def send_text(self, msg, is_at_all=False, at_mobiles=[]):
        """
        text类型
        :param msg: 消息内容
        :param is_at_all: @所有人时：true，否则为false
        :param at_mobiles: 被@人的手机号（字符串）
        :return: 返回消息发送结果
        """
        data = {"msgtype": "text"}
        if is_not_null_and_blank_str(msg):
            data["text"] = {"content": msg}
        else:
            logging.error("text类型，消息内容不能为空！")
            raise ValueError("text类型，消息内容不能为空！")

        if at_mobiles:
            at_mobiles = list(map(str, at_mobiles))

        data["at"] = {"atMobiles": at_mobiles, "isAtAll": is_at_all}
        logging.debug('text类型：%s' % data)
        return self.post(data)

    def send_link(self, title, text, message_url, pic_url=''):
        """
        link类型
        :param title: 消息标题
        :param text: 消息内容（如果太长自动省略显示）
        :param message_url: 点击消息触发的URL
        :param pic_url: 图片URL
        :return: 返回消息发送结果

        """
        if is_not_null_and_blank_str(title) and is_not_null_and_blank_str(text) and is_not_null_and_blank_str(message_url):
            data = {
                    "msgtype": "link",
                    "link": {
                        "text": text,
                        "title": title,
                        "picUrl": pic_url,
                        "messageUrl": message_url
                    }
            }
            logging.debug('link类型：%s' % data)
            return self.post(data)
        else:
            logging.error("link类型中消息标题或内容或链接不能为空！")
            raise ValueError("link类型中消息标题或内容或链接不能为空！")

    def send_markdown(self, title, text, is_at_all=False, at_mobiles=[]):
        """
        markdown类型
        :param title: 首屏会话透出的展示内容
        :param text: markdown格式的消息内容
        :param is_at_all: 被@人的手机号（在text内容里要有@手机号）
        :param at_mobiles: @所有人时：true，否则为：false
        :return: 返回消息发送结果
        """
        if is_not_null_and_blank_str(title) and is_not_null_and_blank_str(text):
            data = {
                "msgtype": "markdown",
                "markdown": {
                    "title": title,
                    "text": text
                },
                "at": {
                    "atMobiles": list(map(str, at_mobiles)),
                    "isAtAll": is_at_all
                }
            }
            logging.debug("markdown类型：%s" % data)
            return self.post(data)
        else:
            logging.error("markdown类型中消息标题或内容不能为空！")
            raise ValueError("markdown类型中消息标题或内容不能为空！")

    def send_action_card(self, action_card):
        """
        ActionCard类型
        :param action_card: 整体跳转ActionCard类型实例或独立跳转ActionCard类型实例
        :return: 返回消息发送结果
        """
        if isinstance(action_card, ActionCard):
            data = action_card.get_data()
            logging.debug("ActionCard类型：%s" % data)
            return self.post(data)
        else:
            logging.error("ActionCard类型：传入的实例类型不正确！")
            raise TypeError("ActionCard类型：传入的实例类型不正确！")

    def send_feed_card(self, links):
        """
        FeedCard类型
        :param links: 信息集（FeedLink数组）
        :return: 返回消息发送结果
        """
        link_data_list = []
        for link in links:
            if isinstance(link, FeedLink) or isinstance(link, CardItem):
                link_data_list.append(link.get_data())
        if link_data_list:
            # 兼容：1、传入FeedLink或CardItem实例列表；2、传入数据字典列表；
            links = link_data_list
        data = {"msgtype": "feedCard", "feedCard": {"links": links}}
        logging.debug("FeedCard类型：%s" % data)
        return self.post(data)

    def post(self, data):
        """
        发送消息（内容UTF-8编码）
        :param data: 消息数据（字典）
        :return: 返回发送结果
        """
        self.times += 1
        if self.times % 20 == 0:
            if time.time() - self.start_time < 60:
                logging.debug('钉钉官方限制每个机器人每分钟最多发送20条，当前消息发送频率已达到限制条件，休眠一分钟')
                time.sleep(60)
            self.start_time = time.time()

        post_data = json.dumps(data)
        try:
            response = requests.post(self.webhook, headers=self.headers, data=post_data)
        except requests.exceptions.HTTPError as exc:
            logging.error("消息发送失败， HTTP error: %d, reason: %s" % (exc.response.status_code, exc.response.reason))
            raise
        except requests.exceptions.ConnectionError:
            logging.error("消息发送失败，HTTP connection error!")
            raise
        except requests.exceptions.Timeout:
            logging.error("消息发送失败，Timeout error!")
            raise
        except requests.exceptions.RequestException:
            logging.error("消息发送失败, Request Exception!")
            raise
        else:
            try:
                result = response.json()
            except:
                logging.error("服务器响应异常，状态码：%s，响应内容：%s" % (response.status_code, response.text))
                return {'errcode': 500, 'errmsg': '服务器响应异常'}
            else:
                logging.debug('发送结果：%s' % result)
                if result['errcode']:
                    error_data = {"msgtype": "text", "text": {"content": "钉钉机器人消息发送失败，原因：%s" % result['errmsg']}, "at": {"isAtAll": True}}
                    logging.error("消息发送失败，自动通知：%s" % error_data)
                    requests.post(self.webhook, headers=self.headers, data=json.dumps(error_data))
                return result


class ActionCard(object):
    """
    ActionCard类型消息格式（整体跳转、独立跳转）
    """
    def __init__(self, title, text, btns, btn_orientation=0, hide_avatar=0):
        """
        ActionCard初始化
        :param title: 首屏会话透出的展示内容
        :param text: markdown格式的消息
        :param btns: 按钮列表类型；
                     按钮数量为1时，整体跳转ActionCard类型，按钮的消息：singleTitle - 单个按钮的方案，singleURL - 点击按钮触发的URL；
                     按钮数量大于1时，独立跳转ActionCard类型，按钮的消息：title - 按钮方案，actionURL - 点击按钮触发的URL；
        :param btn_orientation: 0：按钮竖直排列，1：按钮横向排列
        :param hide_avatar: 0：正常发消息者头像，1：隐藏发消息者头像
        """
        super(ActionCard, self).__init__()
        self.title = title
        self.text = text
        self.btn_orientation = btn_orientation
        self.hide_avatar = hide_avatar
        btn_list = []
        for btn in btns:
            if isinstance(btn, CardItem):
                btn_list.append(btn.get_data())
        if btn_list:
            # 兼容：1、传入CardItem示例列表；2、传入数据字典列表
            btns = btn_list
        self.btns = btns

    def get_data(self):
        """
        获取ActionCard类型消息数据（字典）
        :return: 返回ActionCard数据
        """
        if is_not_null_and_blank_str(self.title) and is_not_null_and_blank_str(self.text) and len(self.btns):
            if len(self.btns) == 1:
                # 独立跳转
                data = {
                        "msgtype": "actionCard",
                        "actionCard": {
                            "title": self.title,
                            "text": self.text,
                            "hideAvatar": self.hide_avatar,
                            "btnOrientation": self.btn_orientation,
                            "singleTitle": self.btns[0]["title"],
                            "singleURL": self.btns[0]["actionURL"]
                        }
                }
                return data
            else:
                # 整体跳转
                data = {
                    "msgtype": "actionCard",
                    "actionCard": {
                        "title": self.title,
                        "text": self.text,
                        "hideAvatar": self.hide_avatar,
                        "btnOrientation": self.btn_orientation,
                        "btns": self.btns
                    }
                }
                return data
        else:
            logging.error("ActionCard类型，消息标题或内容或按钮数量不能为空！")
            raise ValueError("ActionCard类型，消息标题或内容或按钮数量不能为空！")


class FeedLink(object):
    """
    FeedCard类型单条消息格式
    """
    def __init__(self, title, message_url, pic_url):
        """
        初始化单条消息文本
        :param title: 单条消息文本
        :param message_url: 点击单条信息后触发的URL
        :param pic_url: 点击单条消息后面图片触发的URL
        """
        super(FeedLink, self).__init__()
        self.title = title
        self.message_url = message_url
        self.pic_url = pic_url

    def get_data(self):
        """
        获取FeedLink消息数据（字典）
        :return: 本FeedLink消息的数据
        """
        if is_not_null_and_blank_str(self.title) and is_not_null_and_blank_str(self.message_url) and is_not_null_and_blank_str(self.pic_url):
            data = {
                    "title": self.title,
                    "messageURL": self.message_url,
                    "picURL": self.pic_url
            }
            return data
        else:
            logging.error("FeedCard类型单条消息文本、消息链接、图片链接不能为空！")
            raise ValueError("FeedCard类型单条消息文本、消息链接、图片链接不能为空！")


class CardItem(object):
    """
    ActionCard和FeedCard消息类型中的子控件
    """

    def __init__(self, title, url, pic_url=None):
        """
        CardItem初始化
        @param title: 子控件名称
        @param url: 点击子控件时触发的URL
        @param pic_url: FeedCard的图片地址，ActionCard时不需要，故默认为None
        """
        super(CardItem, self).__init__()
        self.title = title
        self.url = url
        self.pic_url = pic_url

    def get_data(self):
        """
        获取CardItem子控件数据（字典）
        @return: 子控件的数据
        """
        if is_not_null_and_blank_str(self.pic_url) and is_not_null_and_blank_str(self.title) and is_not_null_and_blank_str(self.url):
            data = {
                "title": self.title,
                "messageURL": self.url,
                "picURL": self.pic_url
            }
            return data
        elif is_not_null_and_blank_str(self.title) and is_not_null_and_blank_str(self.url):
            data = {
                "title": self.title,
                "actionURL": self.url
            }
            return data
        else:
            logging.error("CardItem是ActionCard的子控件时，title、url不能为空；是FeedCard的子控件时，title、url、pic_url不能为空！")
            raise ValueError("CardItem是ActionCard的子控件时，title、url不能为空；是FeedCard的子控件时，title、url、pic_url不能为空！")

def getmysqlconn():
    config = {
        'host': '114.114.114.114',
        'port': 3306,
        'user': '114',
        'passwd': '114@114.c0m',
        'db': '114',
        'charset': 'utf8',
        'cursorclass': MySQLdb.cursors.DictCursor
    }
    conn = mdb.connect(**config)
    return conn

def getstatusphone(conn):
    mobiles = []
    sql = "SELECT phonenum FROM `accountcontacters` WHERE aegistatus = 1;"
    cursor = conn.cursor()
    try:
        #print 'get status start'
        # 执行sql语句
        cursor.execute(sql)
        # 提交到数据库执行
        results = cursor.fetchall()
        for p in results:
            pn = p.get('phonenum')
            #print pn
            #print pn.__class__
            if pn is None:
                pass
            else:
                mobiles.append(pn)
        #print 'get aegisstatus success'
    except:
        # Rollback in case there is any error
        print 'select aegistatus fail,rollback'
    #print 'aegstatuslist'
    return mobiles

def getarnset(conn):
    arnsets =set()
    cursor = conn.cursor()
    tablelist = ['aegisvullist','eventlist','loginlogs','warninglist','webshelllist']
    for i in tablelist:
        #print i
        sql = "SELECT arn FROM "+i
        try:
            #print 'get arn start'
            # 执行sql语句
            cursor.execute(sql)
            # 提交到数据库执行
            results = cursor.fetchall()
            for j in results:
                #print j
                arnsets.add(j.get('arn').encode("utf-8"))
                #print arnset
            #print 'get arn from tables success'
        except:
            # Rollback in case there is any error
            print 'select arn fail,rollback'
    #print 'arnset'
    return arnsets

def getarninfo(conn,arn):
    sql = "SELECT account,departmentname,phonenum FROM accountcontacters WHERE arn = '" + arn +"'"
    cursor = conn.cursor()
    try:
        #print 'get arninfo start'
        # 执行sql语句
        cursor.execute(sql)
        # 提交到数据库执行
        results = cursor.fetchall()
        #print 'get arninfo success'
    except:
        # Rollback in case there is any error
        print 'select arninfo fail,rollback'
    #print 'arninfo'
    return results[0]

def getloginlog(conn,arn):
    listloginlogs = []
    sql = "SELECT ip,username,loginSourceIp FROM loginlogs WHERE arn = '" + arn +"' and type='crack_success'"
    #print sql
    cursor = conn.cursor()
    try:
        #print 'get loginlogs start'
        # 执行sql语句
        cursor.execute(sql)
        # 提交到数据库执行
        results = cursor.fetchall()
        for i in results:
            log = 'vulip:'+i.get('ip').encode("utf-8")+' username:'+i.get('username').encode("utf-8")+ '\n'+'恶意ip:'+i.get('loginSourceIp').encode("utf-8")
            listloginlogs.append(log)
        #print 'get loginlogs success'
    except:
        # Rollback in case there is any error
        print 'select loginlog fail,rollback'
    #print 'loginloglist'
    print listloginlogs
    return listloginlogs

def getevent(conn,arn):
    listevents = []
    sql = "SELECT ip,description2,description3 FROM eventlist WHERE arn = '" + arn +"' and `level` in ('high','serious')"
    #print sql
    cursor = conn.cursor()
    try:
        #print 'get event start'
        # 执行sql语句
        cursor.execute(sql)
        # 提交到数据库执行
        results = cursor.fetchall()
        for i in results:
            event = 'vulip:'+i.get('ip').encode("utf-8")+ '\n'+'异常事件描述:'+i.get('description2').encode("utf-8")+' '+i.get('description3').encode("utf-8")
            listevents.append(event)
        #print 'get event success'
    except:
        # Rollback in case there is any error
        print 'select event fail,rollback'
    #print 'eventlist'
    #print results
    return  listevents

def getwebshell(conn,arn):
    listwebshells = []
    sql = "SELECT ip,webshell FROM webshelllist WHERE arn = '" + arn +"'"
    #print sql
    cursor = conn.cursor()
    try:
        #print 'get webshell start'
        # 执行sql语句
        cursor.execute(sql)
        # 提交到数据库执行
        results = cursor.fetchall()
        for i in results:
            webshell = 'vulip:'+i.get('ip').encode("utf-8")+ '\n'+'网马地址:\n'+i.get('webshell').encode("utf-8")
            listwebshells.append(webshell)
        #print 'get webshell success'
    except:
        # Rollback in case there is any error
        print 'select webshell fail,rollback'
    #print 'webshelllist'
    return listwebshells

def getvul(conn,arn):
    listvuls = []
    sql = "SELECT uuid,aliasName,level,updatecmd from aegisvullist WHERE arn = '" + arn +"' and (updatecmd like '%bash%' or updatecmd like '%git%')"
    print sql
    cursor = conn.cursor()
    try:
        print 'get vul start'
        # 执行sql语句
        cursor.execute(sql)
        # 提交到数据库执行
        results = cursor.fetchall()
        print 'get vullist end'
        for x in results:
            #print x
            # updatecmd = x.get('updatecmd').encode("utf-8")
            # pattern = re.compile(r'bash|git')
            # match = pattern.match(updatecmd)
            # print match
            uuid= x.get('uuid')
            x.pop('uuid')
            x.pop('updatecmd')
            sql2 = "select publicIpAddress,eipAddress,privateIpAddress,instanceName from cloudhost where serialNumber= '" + uuid +"'"
            try:
                #print 'get ip start'
                # 执行sql语句
                cursor.execute(sql2)
                # 提交到数据库执行
                results2 = cursor.fetchall()
                for y in results2:
                    #print y
                    vul = 'vulip:'+y.get('publicIpAddress').encode("utf-8")+'|'+y.get('eipAddress').encode("utf-8")+'|'+y.get('privateIpAddress').encode("utf-8")+ '\n'+'instanceName:'+y.get('instanceName').encode("utf-8")+ '\n'+'vulname:'+x.get('aliasName').encode("utf-8")+ '\n'+'漏洞等级:'+x.get('level').encode("utf-8")
                    listvuls.append(vul)
                #print 'get vulip success'
            except:
                # Rollback in case there is any error
                print 'select vulip fail,rollback'
        #print 'get vul success'
    except:
        # Rollback in case there is any error
        print 'select vul fail,rollback'
    #print 'vullist'
    #print listvuls
    return listvuls

def getwarn(conn,arn):
    listwarns = []
    sql = "SELECT uuid,riskName,value from warninglist WHERE arn = '" + arn +"' and `level` in ('high','serious')"
    #print sql
    cursor = conn.cursor()
    try:
        #print 'get vul start'
        # 执行sql语句
        cursor.execute(sql)
        # 提交到数据库执行
        results = cursor.fetchall()
        for x in results:
            #print x
            uuid= x.get("uuid")
            x.pop('uuid')
            sql2 = "select publicIpAddress,eipAddress,privateIpAddress,instanceName from cloudhost where serialNumber= '" + uuid +"'"
            try:
                #print 'get ip start'
                # 执行sql语句
                cursor.execute(sql2)
                # 提交到数据库执行
                results2 = cursor.fetchall()
                #print results2
                for y in results2:
                    #print y
                    #print x
                    warn = 'vulip:'+y.get('publicIpAddress').encode("utf-8")+'|'+y.get('eipAddress').encode("utf-8")+'|'+y.get('privateIpAddress').encode("utf-8")+ '\n'+'instanceName:'+y.get('instanceName').encode("utf-8")+ '\n'+x.get('riskName').encode("utf-8")+':'+x.get('value').encode("utf-8")
                    listwarns.append(warn)
                #print 'get warnip success'
            except:
                # Rollback in case there is any error
                print 'select warnip fail,rollback'
        #print 'get warn success'
    except:
        # Rollback in case there is any error
        print 'select warn fail,rollback'
    #print 'warnlist'
    #print listwarn
    return listwarns





if __name__ == '__main__':
    # WebHook地址
    webhook = 'https://oapi.dingtalk.com/robot/send?access_token=122418254aaeb8e0299623056392'
    # 初始化机器人小丁
    xiaoding = DingtalkChatbot(webhook)
    conn = getmysqlconn()
    at_mobiles = getstatusphone(conn)
    if at_mobiles:
        #print at_mobiles
        sendresult = xiaoding.send_text(msg='老师您好，您的安骑士服务已经到期，请及时续费', at_mobiles=at_mobiles)
        print sendresult
    else:
        print '安骑士都正常服务中'

    dictlistname ={'listvul':'服务器漏洞','setevent':'异常事件','setloginlog':'异常登录','listwarn':'基线检查','setwebshell':'webshell网马'}
    arnset = getarnset(conn)
    print arnset
    tag = 0
    listvul = []
    for arn in arnset:
        dictarninfo = getarninfo(conn, arn)
        print dictarninfo
        setloginlog = getloginlog(conn,arn)
        setevent = getevent(conn,arn)
        setwebshell = getwebshell(conn,arn)
        listvul = getvul(conn,arn)
        listwarn = getwarn(conn,arn)
        dictinfo = {'listvul':listvul,'setevent':setevent,'setloginlog':setloginlog,'listwarn':listwarn,'setwebshell':setwebshell}
        msg = ''
        for i in dictinfo:
            info = dictinfo.get(i)
            if info:
                #print info
                msge = dictarninfo.get('departmentname').encode("utf-8")+ '的 '+ dictarninfo.get('account').encode("utf-8")+' 账号需要处理的 '+ dictlistname.get(i) +'类 高危漏洞数量为  '+ str(len(info))+ '\n'+'漏洞详情如下：'
                #print msge
                msg = msg + msge + '\n'
                for detail in info:
                    #print 'asgasdgwertqwrgagdsrwert'
                    print detail
                    msg = msg + detail+ '\n'
                #xiaoding.send_text(msg=msg, is_at_all=False)
            else:
                print arn +'的'+dictlistname.get(i)+'类漏洞不存在'
        # #print 'xiaoxineirong'
        # #print msg
        if msg:
            xiaoding.send_text(msg=msg, at_mobiles=[dictarninfo.get('phonenum').encode("utf-8")])
            tag = 1
        else:
            print 'msg 为空'
        print 'tag'
        print tag
    if tag==1:
        print 'sfasdfwerawerqaw'
        xiaoding.send_text(msg='请各位负责老师根据实际情况处理自己账号的漏洞,并且在安骑士控制台点击相应的处理按钮', is_at_all=True)
    print 'send success'



    # Text消息@所有人
    #xiaoding.send_text(msg='搜有人所有人！', is_at_all=True)


 #   xiaoding.send_markdown(title='安骑士漏洞提醒', text='#### 漏洞提醒\n'
 #                                    '> ![截图](http://www.sinaimg.cn/dy/slidenews/5_img/2013_28/453_28488_469248.jpg)\n'
 #                                    '> ###### 2018-xx-xx 【好未来】漏洞提醒 \n',
 #                 is_at_all=True)




'''
dingding_url = "https://oapi.dingtalk.com/robot/send?access_token=122418254aaeb8e0299623056365bcaae1a6310e7089100a82fec858abe65792" # 这个 url 从 PC 端钉钉群组->管理机器人里获得
headers = {"Content-Type": "application/json; charset=utf-8"}
for i in list:
    phonenum = i.get('phonenum')
    post_data = {
        "msgtype": "text",
        "text": {
            "content": u"老师，您的安骑士服务已到期，请注意续费"
        },
        "at": {
            "atMobiles": [phonenum，sdfwe,13571666,1475822],
    		"isAtAll": false
        }
    }

r = requests.post(dingding_url, headers=headers,data=json.dumps(post_data))
print(r.content)
'''








