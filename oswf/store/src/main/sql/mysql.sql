
# Tables --------------------------------------------------------------------------------------

create table currentstep (
    id bigint not null auto_increment,
    piid bigint,
    step integer,
    owner varchar(255),
    status varchar(255),
    startdate datetime,
    duedate datetime,
    primary key (id)
) ENGINE=InnoDB;

create table historystep (
    id bigint not null auto_increment,
    piid bigint,
    step integer,
    owner varchar(255),
    status varchar(255),
    startdate datetime,
    duedate datetime,
    actor varchar(255),
    action integer,
    finishdate datetime,
    primary key (id)
) ENGINE=InnoDB;

create table processinstance (
    piid bigint not null auto_increment,
    version integer not null,
    wfname varchar(255),
    wfstate integer,
    primary key (piid)
) ENGINE=InnoDB;

create table propertyset (
    piid bigint not null,
    itemkey varchar(255) not null,
    stringval varchar(255),
    intval integer,
    doubleval double precision,
    longval bigint,
    textval text,
    itemtype integer,
    primary key (piid, itemkey)
) ENGINE=InnoDB;

create table xmldescriptor (
    id int not null auto_increment,
    wfname varchar(255),
    content text,
    primary key (id)
) ENGINE=InnoDB;

# Relationships ------------------------------------------------------------------------------

alter table currentstep 
    add index FK_CURRENTSTEP_PIID (piid), 
    add constraint FK_CURRENTSTEP_PIID 
    foreign key (piid) 
    references processinstance (piid);

alter table historystep 
    add index FK_HISTORYSTEP_PIID (piid), 
    add constraint FK_HISTORYSTEP_PIID 
    foreign key (piid) 
    references processinstance (piid);

alter table propertyset 
    add index FK_PROPERTYSET_PIID (piid), 
    add constraint FK_PROPERTYSET_PIID 
    foreign key (piid) 
    references processinstance (piid);
