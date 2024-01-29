create table Chart
(
    id         bigint auto_increment
        primary key,
    name       varchar(64)                         not null comment '图标名称',
    goal       varchar(256)                        null comment '用户目的',
    chartData  varchar(1024)                       null,
    chartType  varchar(64)                         null,
    genChart   varchar(5120)                       null,
    genResult  varchar(1024)                       null,
    userId     bigint                              not null,
    createTime timestamp default CURRENT_TIMESTAMP not null,
    updateTime timestamp                           null,
    isDelete   tinyint   default 0                 not null,
    status     varchar(16)                         not null
);

create index Chart_User_id_fk
    on Chart (userId);

create table History
(
    id           bigint auto_increment
        primary key,
    askContent   varchar(512)                        not null,
    replyContent varchar(5120)                       null,
    userId       bigint                              not null,
    createTime   timestamp default CURRENT_TIMESTAMP not null,
    modelId      mediumtext                          not null,
    isDelete     tinyint   default 0                 not null
);

create table User
(
    id           bigint auto_increment
        primary key,
    userAccount  varchar(64)                           null,
    userPassword varchar(256)                          null,
    userName     varchar(64) default 'GPTNewUser'       null,
    userAvatar   varchar(256)                          null,
    userRole     varchar(64)                           null,
    createTime   timestamp   default CURRENT_TIMESTAMP not null,
    updateTime   timestamp   default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    isDelete     tinyint     default 0                 not null
);

