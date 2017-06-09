CREATE TABLE `processinstance` (
  `piid` bigint(20) NOT NULL auto_increment,
  `wfname` varchar(255) default NULL,
  `wfstate` int(11) default NULL,
  PRIMARY KEY  (`piid`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;


CREATE TABLE `currentstep` (
  `id` bigint(20) NOT NULL auto_increment,
  `piid` bigint(20) default NULL,
  `step` int(11) default NULL,
  `owner` varchar(255) default NULL,
  `status` varchar(255) default NULL,
  `startdate` datetime default NULL,
  `duedate` datetime default NULL,
  PRIMARY KEY  (`id`),
  KEY `FK_CURRENTSTEP_PIID` (`piid`),
  CONSTRAINT `FK_CURRENTSTEP_PIID` FOREIGN KEY (`piid`) REFERENCES `processinstance` (`piid`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;


CREATE TABLE `historystep` (
  `id` bigint(20) NOT NULL auto_increment,
  `piid` bigint(20) default NULL,
  `step` int(11) default NULL,
  `owner` varchar(255) default NULL,
  `status` varchar(255) default NULL,
  `startdate` datetime default NULL,
  `duedate` datetime default NULL,
  `actor` varchar(255) default NULL,
  `action` int(11) default NULL,
  `finishdate` datetime default NULL,
  PRIMARY KEY  (`id`),
  KEY `FK_HISTORYSTEP_PIID` (`piid`),
  CONSTRAINT `FK_HISTORYSTEP_PIID` FOREIGN KEY (`piid`) REFERENCES `processinstance` (`piid`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;


CREATE TABLE `processvariable` (
  `piid` bigint(20) NOT NULL,
  `itemkey` varchar(255) NOT NULL,
  `stringval` varchar(255) default NULL,
  `intval` int(11) default NULL,
  `doubleval` double default NULL,
  `longval` bigint(20) default NULL,
  `textval` text,
  `itemtype` int(11) default NULL,
  PRIMARY KEY  (`piid`,`itemkey`),
  KEY `FK_PROCESSVARIABLE_PIID` (`piid`),
  CONSTRAINT `FK_PROCESSVARIABLE_PIID` FOREIGN KEY (`piid`) REFERENCES `processinstance` (`piid`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


CREATE TABLE `xmldescriptor` (
  `id` bigint(20) NOT NULL auto_increment,
  `wfname` varchar(255) NOT NULL,
  `content` longtext,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;