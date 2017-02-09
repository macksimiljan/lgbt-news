CREATE TABLE IF NOT EXISTS newspaper (
  id  TINYINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
  name  NVARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS queryterm (
  id TINYINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
  term  NVARCHAR(50)
);

CREATE TABLE IF NOT EXISTS document (
  id  INT NOT NULL PRIMARY KEY,
  nytidentifier CHAR(24),
  url NVARCHAR(150),
  snippet NVARCHAR(250),
  abstract NVARCHAR(1000),
  printpage NVARCHAR(20),
  source NVARCHAR(100),
  headline NVARCHAR(100),
  kicker NVARCHAR(100),
  pubdate NCHAR(20),
  type NVARCHAR(20),
  byline NVARCHAR(200),
  materialtype NVARCHAR(20),
  wordcount SMALLINT,
  id_newspaper TINYINT NOT NULL,
  id_queryterm TINYINT NOT NULL,
  FOREIGN KEY fk_newspaper(id_newspaper) REFERENCES newspaper(id)
    ON UPDATE CASCADE
    ON DELETE RESTRICT,
  FOREIGN KEY fk_queryterm(id_queryterm) REFERENCES queryterm(id)
    ON UPDATE CASCADE
    ON DELETE RESTRICT
);

CREATE TABLE IF NOT EXISTS leadparagraph (
  id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
  id_document INT NOT NULL,
  text NVARCHAR(2500) NOT NULL,
  FOREIGN KEY fk_document(id_document) REFERENCES document(id)
    ON UPDATE CASCADE
    ON DELETE RESTRICT
);

CREATE TABLE IF NOT EXISTS keyword (
  id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
  name NVARCHAR(50) NOT NULL,
  value NVARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS document_keyword (
  id_document INT NOT NULL,
  id_keyword INT NOT NULL,
  PRIMARY KEY (id_document, id_keyword),
  FOREIGN KEY fk_document(id_document) REFERENCES document(id)
    ON UPDATE CASCADE
    ON DELETE RESTRICT,
  FOREIGN KEY fk_keyword(id_keyword) REFERENCES keyword(id)
    ON UPDATE CASCADE
    ON DELETE RESTRICT
);
