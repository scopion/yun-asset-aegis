/*
Navicat MariaDB Data Transfer



Target Server Type    : MariaDB
Target Server Version : 50556
File Encoding         : 65001

Date: 2018-10-29 15:25:09
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for loginlogs
-- ----------------------------
DROP TABLE IF EXISTS `loginlogs`;
CREATE TABLE `loginlogs` (
  `id` int(5) NOT NULL AUTO_INCREMENT,
  `arn` varchar(50) NOT NULL,
  `instanceId` varchar(50) NOT NULL,
  `ip` varchar(20) NOT NULL,
  `username` varchar(20) DEFAULT NULL,
  `loginSourceIp` varchar(20) NOT NULL,
  `type` varchar(25) NOT NULL,
  `time` varchar(24) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=30 DEFAULT CHARSET=utf8;
