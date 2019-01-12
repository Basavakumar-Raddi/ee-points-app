prerequisites for running the app :
    create a database schema with name "A1  " in mysql(5.6) and run the below query to create a table
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
    have set the property "spring.jpa.generate-ddl=true" for creating table just in case.
    Requires java-8

Run the app as a spring boot application (on default port - 8080)

The api's exposed are
view points (http-get) -- http://localhost:8080/viewPoints
add point (http-post) -- http://localhost:8080/addPoint
    sample json : {"measurementDay": "2018-12-17", "location": "FI", "value": 15}
delete api (http-delete) -- http://localhost:8080/deletePoint?id=0c157350-aabd-4c55-a42e-6e2de16b195b
    pass the id of the point to be deleted

This app also has the junit test cases written for all the above api's, service class and repository class.