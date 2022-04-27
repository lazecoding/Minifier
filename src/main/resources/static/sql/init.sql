DROP TABLE IF EXISTS `url_map`;

CREATE TABLE `url_map` (
   `conversion_code` varchar(11) NOT NULL,
   `full_url` varchar(765) NOT NULL,
   `ttl` bigint(20) NOT NULL,
   `create_time` timestamp NOT NULL DEFAULT NOW(),
   PRIMARY KEY (`conversion_code`),
   INDEX idx_full_url (`full_url`)
) ENGINE = InnoDB;