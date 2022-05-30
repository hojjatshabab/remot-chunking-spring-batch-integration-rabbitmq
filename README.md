# remot-chunking-spring-batch-integration-rabbitmq

please run rebbitmq on docker 
  1.docker pull rabbitmq:3-management
  2.docker run --rm -it -p 15672:15672 -p 5672:5672 rabbitmq:3-management
login in rabbitMQ with username and password : quest  
you should create two queues in rabbitMQ with names: replies and requests

you should create database "remote-chunk" in postgresql
and create table "transaction" with sql code : 
CREATE TABLE IF NOT EXISTS public.transaction
(
    account character varying COLLATE pg_catalog."default",
    amount character varying COLLATE pg_catalog."default",
    "timestamp" character varying COLLATE pg_catalog."default"
)

run master-remote-chunking
run worker-remote-chunking

run the job with:
http://localhost:8000/load

finish
  
