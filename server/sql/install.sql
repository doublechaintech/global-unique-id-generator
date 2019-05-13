CREATE TABLE `id_space` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `space_name` varchar(20) NOT NULL COMMENT 'ID空间名称',
  `prefix` varchar(10) DEFAULT NULL COMMENT '前缀',
  `seed` bigint(20) NOT NULL DEFAULT '0' COMMENT '起始',
  `steps` int(11) NOT NULL COMMENT '步长',
  `replenish_threshold` int(11) NOT NULL COMMENT '扩充ID的阈值',
  `seq_length` int(11) DEFAULT NULL COMMENT 'ID的sequence部分的长度',
  `suffix` varchar(10) DEFAULT NULL COMMENT '后缀',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_spacename` (`space_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;