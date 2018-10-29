#-*- encoding: utf-8 -*-
#!/usr/bin/env python
import MySQLdb as mdb
import hashlib, json, httplib
import urlparse
import urllib
import sys,datetime
import requests
import MySQLdb.cursors

class UCLOUDException(Exception):
    def __str__(self):
        return "Error"


def _verfy_ac(private_key, params):
    items = params.items()
    items.sort()

    params_data = ""
    for key, value in items:
        params_data = params_data + str(key) + str(value)

    params_data = params_data+private_key
    
    '''use sha1 to encode keys'''
    hash_new = hashlib.sha1()
    hash_new.update(params_data)
    hash_value = hash_new.hexdigest()
    return hash_value


class UConnection(object):
    def __init__(self, base_url):
        self.base_url = base_url
        o = urlparse.urlsplit(base_url)
        if o.scheme == 'https':
            self.conn = httplib.HTTPSConnection(o.netloc)
        else:
            self.conn = httplib.HTTPConnection(o.netloc)

    def __del__(self):
        self.conn.close()

    def get(self, resouse, params):
        # type: (object, object) -> object
        resouse += "?" + urllib.urlencode(params)
        print("%s%s" % (self.base_url, resouse))
        self.conn.request("GET", resouse)
        response = json.loads(self.conn.getresponse().read())
        return response

    def post(self, uri, params):
        print("%s%s %s" % (self.base_url, uri, params))
        headers = {"Content-Type": "application/json"}
        self.conn.request("POST", uri, json.JSONEncoder().encode(params), headers)
        response = json.loads(self.conn.getresponse().read())
        return response


class UcloudApiClient(object):
    # 添加 设置 数据中心和  zone 参数
    def __init__(self, base_url, public_key, private_key):
        self.g_params = {}
        self.g_params['PublicKey'] = public_key
        self.private_key = private_key
        self.conn = UConnection(base_url)

    def get(self, uri, params):
        # type: (object, object) -> object
        # print params
        _params = dict(self.g_params, **params)

#        if project_id : 
#            _params["ProjectId"] = project_id

        _params["Signature"] = _verfy_ac(self.private_key, _params)
        return self.conn.get(uri, _params)

    def post(self, uri, params):
        _params = dict(self.g_params, **params)

#        if project_id :
#            _params["ProjectId"] = project_id

        _params["Signature"] = _verfy_ac(self.private_key, _params)
        return self.conn.post(uri, _params)


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


def insertorupdate(conn,instanceId,instanceName,privateIpAddress,publicIpAddress,eipAddress,OSNEnvironment,OSName,regionId,serialNumber,updateTime,status,arn):
    # dbucloudlist = []
    cursor = conn.cursor()
    # SQL update sql
    sql = """insert into cloudhost(instanceId,instanceName,privateIpAddress,publicIpAddress,eipAddress,OSNEnvironment,OSName,regionId,serialNumber,updateTime,status,arn) values(%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s) on duplicate key update instanceName=VALUES(instanceName),privateIpAddress=VALUES(privateIpAddress),publicIpAddress=VALUES(publicIpAddress),eipAddress=VALUES(eipAddress),OSNEnvironment=VALUES(OSNEnvironment),OSName=VALUES(OSName),regionId=VALUES(regionId),serialNumber=VALUES(serialNumber),updateTime=VALUES(updateTime),status=VALUES(status),arn=VALUES(arn)"""

    values = (instanceId,instanceName,privateIpAddress,publicIpAddress,eipAddress,OSNEnvironment,OSName,regionId,serialNumber,updateTime,status,arn)
    try:
        print 'update sql start'
        # 执行sql语句
        n = cursor.execute(sql, values)
        print n
        # 提交到数据库执行
        conn.commit()
        print 'update success'
    except:
        # Rollback in case there is any error
        conn.rollback()
        print 'update fail,rollback'

    '''
    r = cursor.fetchall()
    print r
    print r.__class__
    for i in r:
        print i
        print i.get('hostid')
        hostid = i.get('hostid').encode("utf-8")
        dbucloudlist.append(hostid)

    return dbucloudlist
    '''

def insertorupcount(conn,arn,instancenum):
    # dbucloudlist = []
    cursor = conn.cursor()
    # SQL update sql
    sql = """insert into accountcontacters(arn,instancenum) values(%s,%s) on duplicate key update arn=VALUES(arn),instancenum=VALUES(instancenum)"""

    values = (arn,instancenum)
    try:
        print 'update sql start'
        # 执行sql语句
        n = cursor.execute(sql, values)
        print n
        # 提交到数据库执行
        conn.commit()
        print 'update success'
    except:
        # Rollback in case there is any error
        conn.rollback()
        print 'update fail,rollback'

	
def delinstance(conn):
    sql = "delete from cloudhost where updateTime <(select date_sub(now(),interval 1 week))"
    cursor = conn.cursor()
    try:
        print 'del sql start'
        # 执行sql语句
        cursor.execute(sql)
        # 提交到数据库执行
        conn.commit()
        print 'del success'
    except:
        # Rollback in case there is any error
        conn.rollback()
        print 'del fail,rollback'


#配置公私钥"""
public_key  = "sZgG345wertqwet5weger7w4574yewry3456234twqt="
private_key = "aasdfasrwqercc514"
base_url    = "https://api.ucloud.cn"

#listproject = []
# listyunhostid = []
listregion = ['cn-bj1','cn-bj2','us-ca','cn-sh2','hk']

'''

#loginurl = 'http://10.1.30.181:8888/login/'


# posturl = 'http://10.1.28.95:8888/asset/api/'
# postdata ={}
#实例化 API 句柄
bnamedict = {};
bnamedict.setdefault("ousr","13")
bnamedict.setdefault("oevv","29")
bnamedict.setdefault("o5st","32")
bnamedict.setdefault("oiat","14")
bnamedict.setdefault("oq1q","39")
bnamedict.setdefault("ojk4","32")
bnamedict.setdefault("oyu5","15")
bnamedict.setdefault("oc3t","33")
bnamedict.setdefault("oebw","32")

# bnamedict.setdefault("ortwerywyw","阿斯蒂芬噶人")
# bnamedict.setdefault("ortwerywyw","阿斯蒂芬噶人")
# bnamedict.setdefault("ortwerywyw","阿斯蒂芬噶人")
# bnamedict.setdefault("ortwerywyw","阿斯蒂芬噶人")
# bnamedict.setdefault("ortwerywyw","AI阿斯蒂芬噶人")
# bnamedict.setdefault("ortwerywyw","阿斯蒂芬噶人")
# bnamedict.setdefault("ortwerywyw","阿斯蒂芬噶人")
# bnamedict.setdefault("ortwerywyw","阿斯蒂芬噶人")
# bnamedict.setdefault("owertwerywyw","阿斯蒂芬噶人")
'''



#调用
if __name__=='__main__':
    arg_length = len(sys.argv)
    ApiClient = UcloudApiClient(base_url, public_key, private_key)


    #登录系统
    # session = requests.session()
    # session.post(loginurl,logindata)
    # session.post(posturl, postdata)

    #获取list,不实现
    #获取ProjectList
    listprojectidresponse = ApiClient.get("/", {"Action":"GetProjectList"});
    # print listprojectidresponse
    # print listprojectidresponse.__class__
    # print listprojectidresponse.get('ProjectSet')
    # print listprojectidresponse.get('ProjectSet').__class__
    listprojectset = listprojectidresponse.get('ProjectSet')
    print listprojectset
    #循环projectid
    for projectidindex in listprojectset:
        #获取单个projectid
        projectid = projectidindex.get('ProjectId')
        print projectid
        instancenum = 0
        #循环regionid
        for regionid in listregion:
            print  regionid
            Parameters = {
                "Action": "DescribeUHostInstance",
                # "Action":"GetRegion",
                "ProjectId": projectid,  # 项目ID 请在Dashbord 上获
                "Region": regionid,
            }
            #获取此项目此地区机器表
            response = ApiClient.get("/", Parameters);
            count = response.get('TotalCount')
            #print response
            listhost = response.get('UHostSet')
            print count
            instancenum = instancenum + count
            insertorupcount(conn,projectid,instancenum)
            print listhost
            #循环获取单个机器信息
            if count:
                for i in listhost:
                    print i.encode("utf-8")
                    instanceId = i.get('UHostId').encode("utf-8")
                    instanceName = i.get('Remark').encode("utf-8")
                    privateIpAddress = i.get('IPSet')[0].get('IP').encode("utf-8")
                    publicIpAddress = i.get('IPSet')[1].get('IP').encode("utf-8")
                    eipAddress = ""
                    OSNEnvironment = 'ucloud'
                    OSName = i.get('BasicImageName').encode("utf-8")
                    regionId = i.get('Zone').encode("utf-8")
                    serialNumber =""
                    updateTime = datetime.datetime.now().strftime("%Y-%m-%d %H:%M:%S")
                    status = i.get('State').encode("utf-8")
                    arn = projectid

                    '''
                  postdata = {
                      'hostid':  hostid,
                      'bname': bname,
                      'domain_title':  domain_title,
                      'os_env': os_env,
                      'domain': domain,
                      'innerIp': innerIp,
                      'outIp': outIp,
                      'os_name':  os_name,
                      'os_locate':  os_locate,
                      'status': status,
                      'os_sec': os_sec
                  }
                  print hostid
                  listyunhostid.append(hostid)
                  print os_sec
                  #调用API，插入数据库
                  postresult = session.post(posturl,postdata)
                  print postresult
                  print postresult.text
                  #将ID添加到list
                  '''
                    conn = getmysqlconn()
                    insertorupdate(conn,instanceId,instanceName,privateIpAddress,publicIpAddress,eipAddress,OSNEnvironment,OSName,regionId,serialNumber,updateTime,status,arn)
                print projectid + '  done in ' + regionid
            else:
                pass
        print projectid + '  done'
    print 'all project done'
    delinstance(conn)
    conn.close()
    print 'job done'


    '''
    mysqlconn = getmysqlconn()
    listdbhostid = getdbhostidlist(mysqlconn)
    print listdbhostid
    在 db  中 不在 yun 中
    retdel = list(set(listdbhostid).difference(set(listyunhostid)))
    print "retD is: ", retdel
    if retdel:
        print 'db old,sql del'
        for i in retdel:
            print 'del '+ i
            print i.__class__
            deldbhostid(mysqlconn,i)
    print 'close mysql'
    mysqlconn.close()
    '''











