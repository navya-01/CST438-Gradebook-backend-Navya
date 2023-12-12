create table user_alias (
                            alias varchar(25) primary key,
                            attempts int,
                            correct int,
                            consecutive_correct int,
                            level varchar(20)
);
