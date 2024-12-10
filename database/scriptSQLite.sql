-- Tabla usuarios
CREATE TABLE usuarios
(
    usuario          VARCHAR(15) PRIMARY KEY,
    nombre           VARCHAR(100) NOT NULL,
    apellido         VARCHAR(100) NOT NULL,
    fecha_nacimiento VARCHAR(10)  NOT NULL,
    email            VARCHAR(200) NOT NULL,
    pass             varchar(500) NOT NULL
);

insert into  usuarios values ('admin', 'irene', 'sarasua','1996-04-26', 'irene.sarasua@ikasle.egibide.org', '1234');