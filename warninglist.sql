/*
Navicat MariaDB Data Transfer



Target Server Type    : MariaDB
Target Server Version : 50556
File Encoding         : 65001

Date: 2018-10-29 15:25:22
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for warninglist
-- ----------------------------
DROP TABLE IF EXISTS `warninglist`;
CREATE TABLE `warninglist` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `uuid` varchar(50) NOT NULL,
  `arn` varchar(60) NOT NULL,
  `riskName` varchar(50) NOT NULL,
  `level` varchar(8) NOT NULL,
  `checkItem` varchar(50) DEFAULT NULL,
  `value` varchar(500) DEFAULT NULL,
  `solution1` varchar(255) NOT NULL,
  `solution2` varchar(255) DEFAULT NULL,
  `time` varchar(24) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
