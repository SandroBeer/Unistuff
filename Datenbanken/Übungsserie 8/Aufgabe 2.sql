DROP TABLE Futter, Kamin, Karton, Laptop, Hund, Lieblingsplatz, Haustier, "hat-bei", Katze, Person, Halter, Aufpasser, HaustierFutter;

CREATE TABLE Futter (Hersteller VARCHAR(100),
					 Name VARCHAR(100),
					 PRIMARY KEY (Hersteller, Name));

CREATE TABLE Kamin (lid INTEGER PRIMARY KEY,
				   Material VARCHAR(100));
				   
CREATE TABLE Karton (lid INTEGER PRIMARY KEY);

CREATE TABLE Laptop (lid INTEGER PRIMARY KEY, 
					 Hersteller VARCHAR(100), 
					 Kennzeichnung VARCHAR(100));
		
CREATE TABLE Hund (hid INTEGER PRIMARY KEY,
				   Rasse VARCHAR(100));
				   
CREATE TABLE Lieblingsplatz (lid INTEGER PRIMARY KEY);
				   
CREATE TABLE Haustier (hid INTEGER PRIMARY KEY,
					  Name VARCHAR(100),
					  GebTag INTEGER,
					  GebMonat INTEGER,
					  GebJahr INTEGER);

CREATE TABLE "hat-bei" (hid INTEGER,
					 pid INTEGER,
					 lid INTEGER,
					 PRIMARY KEY (hid, pid));

CREATE TABLE Katze (hid INTEGER PRIMARY KEY, Dominanz INTEGER, Farbe VARCHAR(100));

CREATE TABLE Person (pid INTEGER PRIMARY KEY, Name VARCHAR(100), Wohnort VARCHAR(100));

CREATE TABLE Halter (pid INTEGER PRIMARY KEY, Typ VARCHAR(100));

CREATE TABLE Aufpasser (pid INTEGER PRIMARY KEY, Studenlohn INTEGER);

-- mit dem nächsten Statement wird das fehlende Verknüpfungsattribut bei "gehört" ergänzt
ALTER TABLE Haustier ADD pid INTEGER;

-- mit diesem Statement wird die fehlende "mag"-Tabelle ergänzt
CREATE TABLE HaustierFutter (hid INTEGER,
							 Hersteller VARCHAR(100),
							 Name VARCHAR(100),
							 PRIMARY KEY (hid, Hersteller, Name));