/*
Navicat MariaDB Data Transfer



Target Server Type    : MariaDB
Target Server Version : 50556
File Encoding         : 65001

Date: 2018-10-29 15:24:54
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for eventlist
-- ----------------------------
DROP TABLE IF EXISTS `eventlist`;
CREATE TABLE `eventlist` (
  `id` int(5) NOT NULL AUTO_INCREMENT,
  `arn` varchar(50) NOT NULL,
  `instanceId` varchar(50) NOT NULL,
  `ip` varchar(20) NOT NULL,
  `level` varchar(8) NOT NULL,
  `description1` varchar(255) NOT NULL,
  `description2` varchar(1600) DEFAULT NULL,
  `description3` varchar(1600) DEFAULT NULL,
  `description4` varchar(5000) DEFAULT NULL,
  `description5` varchar(500) DEFAULT NULL,
  `time` varchar(24) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8;
