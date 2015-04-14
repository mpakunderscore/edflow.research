# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table categories (
  name                      varchar(255) not null,
  categories                TEXT,
  constraint pk_categories primary key (name))
;

create table pages (
  id                        bigint not null,
  url                       varchar(255),
  title                     varchar(255),
  tokens                    TEXT,
  categories                TEXT,
  constraint pk_pages primary key (id))
;

create table tokens (
  name                      varchar(255) not null,
  redirect                  varchar(255),
  categories                TEXT,
  mark                      boolean,
  constraint pk_tokens primary key (name))
;

create sequence categories_seq;

create sequence pages_seq;

create sequence tokens_seq;




# --- !Downs

drop table if exists categories cascade;

drop table if exists pages cascade;

drop table if exists tokens cascade;

drop sequence if exists categories_seq;

drop sequence if exists pages_seq;

drop sequence if exists tokens_seq;

