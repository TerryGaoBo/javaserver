CREATE TABLE IF NOT EXISTS `allkeys` (
  `guid` binary(27) NOT NULL,
  `val` blob NOT NULL,
  PRIMARY KEY (`guid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `guild` (
  `guid` binary(27) NOT NULL,
  `val` blob NOT NULL,
  PRIMARY KEY (`guid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `role` (
  `guid` binary(27) NOT NULL,
  `net` smallint(5) NOT NULL,
  `channel` varchar(20) NOT NULL,
  `userId` char(50) NOT NULL,
  `name` char(50) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `charId` int(11) NOT NULL DEFAULT '0',
  `level` int(11) NOT NULL DEFAULT '0',
  `firstLogin` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `lastLogin` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
   `vipScore` int(11) NOT NULL DEFAULT '0',
   `paid` tinyint(2) NOT NULL DEFAULT '0',
   `power` int(11) NOT NULL DEFAULT '0',
   `heroPow` int(11) NOT NULL DEFAULT '0',
   `gold` int(11) NOT NULL DEFAULT '0',
   `silver` int(11) NOT NULL DEFAULT '0',
   `coin` int(11) NOT NULL DEFAULT '0',
   `exp` int(11) NOT NULL DEFAULT '0',
   `examLv` int(11) NOT NULL DEFAULT '0',
   `chapter` int(11) NOT NULL DEFAULT '0',
   `stage` int(11) NOT NULL DEFAULT '0',
   `teamName` char(50) NOT NULL,
  PRIMARY KEY (`guid`),
  UNIQUE KEY `name` (`name`),
  KEY `userId` (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `pay` (
  `guid` binary(27) NOT NULL,
  `userId` char(50) NOT NULL,
  `channel` varchar(20) NOT NULL,
  `status` tinyint(2) NOT NULL DEFAULT '0',
  `logTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `itemId` char(50) NOT NULL,
  `orderId` char(50) NOT NULL,
  `rmb` int(11) NOT NULL,
  `exchange` int(11) NOT NULL,
  `bouns` int(11) NOT NULL,
  `param` char(255),
  KEY (`guid`),
  KEY `userId` (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `team` (
  `name` varchar(25) NOT NULL,
  `val` blob NOT NULL,
  PRIMARY KEY (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


--ALTER TABLE `balli`.`role` ADD COLUMN `stage` INT(11) DEFAULT 0 AFTER `chapter`;