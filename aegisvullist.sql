/*
Navicat MariaDB Data Transfer



Target Server Type    : MariaDB
Target Server Version : 50556
File Encoding         : 65001

Date: 2018-10-29 15:24:27
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for aegisvullist
-- ----------------------------
DROP TABLE IF EXISTS `aegisvullist`;
CREATE TABLE `aegisvullist` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `uuid` varchar(50) NOT NULL,
  `arn` varchar(60) NOT NULL,
  `name` varchar(40) NOT NULL,
  `aliasName` varchar(100) DEFAULT NULL,
  `necessity` varchar(5) DEFAULT NULL,
  `level` varchar(8) NOT NULL,
  `cvelist` varchar(500) DEFAULT NULL,
  `updatecmd` varchar(250) DEFAULT NULL,
  `time` varchar(24) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=357 DEFAULT CHARSET=utf8;
