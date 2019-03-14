-- create table horse if not exists
CREATE TABLE IF NOT EXISTS horse (
  -- use auto incrementing IDs as primary key
  id        BIGINT AUTO_INCREMENT PRIMARY KEY,
  name      VARCHAR(255) NOT NULL,
  breed     TEXT,
  min_speed DOUBLE       NOT NULL,
  max_speed DOUBLE       NOT NULL,
  created   DATETIME     NOT NULL,
  updated   DATETIME     NOT NULL
);

--create table jockey if not exists
CREATE TABLE IF NOT EXISTS jockey (
  -- use auto incrementing IDs as primary key
  id        BIGINT AUTO_INCREMENT PRIMARY KEY,
  name      VARCHAR(255) NOT NULL,
  skill     DOUBLE       NOT NULL,
  created   DATETIME     NOT NULL,
  updated   DATETIME     NOT NULL
);

--create table simulation if not exists
CREATE TABLE IF NOT EXISTS simulation (
  -- use auto incrementing IDs as primary key
  id        BIGINT AUTO_INCREMENT PRIMARY KEY,
  name      VARCHAR(255) NOT NULL,
  created   DATETIME     NOT NULL
);

CREATE TABLE IF NOT EXISTS  hj_combination (
  id            BIGINT AUTO_INCREMENT PRIMARY KEY,
  rank          BIGINT          NOT NULL,
  horse_Name    VARCHAR(255)    NOT NULL,
  jockey_Name   VARCHAR(255)    NOT NULL,
  avg_Speed     DOUBLE          NOT NULL,
  horse_Speed   DOUBLE          NOT NULL,
  jockey_Skill  DOUBLE          NOT NULL,
  luckFactor    DOUBLE          NOT NULL,
  simulationID  BIGINT          NOT NULL
)