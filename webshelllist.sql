/*
Navicat MariaDB Data Transfer



Target Server Type    : MariaDB
Target Server Version : 50556
File Encoding         : 65001

Date: 2018-10-29 15:25:34
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for webshelllist
-- ----------------------------
DROP TABLE IF EXISTS `webshelllist`;
CREATE TABLE `webshelllist` (
  `id` int(5) NOT NULL AUTO_INCREMENT,
  `arn` varchar(50) DEFAULT NULL,
  `instanceId` varchar(50) NOT NULL,
  `ip` varchar(20) NOT NULL,
  `webshell` varchar(255) NOT NULL,
  `time` varchar(24) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8;
