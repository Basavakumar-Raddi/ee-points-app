use point_app;
CREATE TABLE `point` (
	`id` VARCHAR(255) NOT NULL,
	`created_by` VARCHAR(255) NOT NULL,
	`created_on` DATETIME NOT NULL,
	`last_updated_by` VARCHAR(255) NULL DEFAULT NULL,
	`last_updated_on` DATETIME NULL DEFAULT NULL,
	`measurement_location` VARCHAR(255) NULL DEFAULT NULL,
	`measurement_day` BIGINT(20) NULL DEFAULT NULL,
	`measurement_value` DOUBLE(20,2) NULL DEFAULT NULL,
	PRIMARY KEY (`id`),
	UNIQUE INDEX `location-day` (`measurement_location`, `measurement_day`)
)

