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
  luckFactor    FLOAT          NOT NULL,
  horseId       BIGINT          NOT NULL,
  jockeyId      BIGINT          NOT NULL,
  simulationID  BIGINT          NOT NULL REFERENCES simulation(id)
);

CREATE TABLE IF NOT EXISTS horseHistory(
  id        BIGINT AUTO_INCREMENT PRIMARY KEY,
  horseId   BIGINT,
  name      VARCHAR(255) NOT NULL,
  breed     TEXT,
  min_speed DOUBLE       NOT NULL,
  max_speed DOUBLE       NOT NULL,
  updated   DATETIME     NOT NULL,
  deleted   BOOLEAN      NOT NULL DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS jockeyHistory (
  id        BIGINT AUTO_INCREMENT PRIMARY KEY,
  jockeyId  BIGINT,
  name      VARCHAR(255) NOT NULL,
  skill     DOUBLE       NOT NULL,
  updated   DATETIME     NOT NULL,
  deleted   BOOLEAN      NOT NULL DEFAULT FALSE
);


