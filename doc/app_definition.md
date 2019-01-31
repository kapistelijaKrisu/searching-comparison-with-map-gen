# Ohjelman määrittelydokumentti

## Ohjelman perustiedot
* Kieli: Java 11
* Gradle 5
* Komentorivi ohjelma

## Päävalikko
* Aseta kartta
* Valitse ajettava algoritmi
* Tuntemattomasta syötteestä ohjelma suljetaan.
* (optional) Luo .map tiedosto käytetystä kartasta
* (optional) Avaa raportti X ohjelmalla. todennäköisesti html ja chromella
* (optional) Käy monta testi kierrosta läpi kerralla

## Ohjelman peruskulku
* askel 1: Aseta kartta
* askel 2: Valitse algoritmi
* askel 3: ?????
* askel 4: Profit. Juurikansion viereen on ilmestynyt doc/reports kansio (jos sitä ei ole), jonka sisällä on raportti algoritmin tuloksista.

### 1 Aseta kartta

#### 1.1 Karttagenetaattorin valinta
* Sisälle rakennettu NoWeightSimpleGenerator alkuvaiheiden testausta varten
* Rakennetaan samanlainen random painojen kanssa.
* Varsinaista analyysiä varten luodaan lukija
* Joskus harmaassa tulevaisuudessa voisi luoda graafinen generaattori millä voi piirtää haluttu testikartta (Ei tira labran scopessa)


##### Kartan luominen lukijalla
* Ohjelman juurikansion vieressä on maps niminen kansio
* maps kansiosta valitaan .map tyyppinen tiedosto
##### .map rakenne
* tiedosto muotoiltu siten, että jokaiselle merkille on annettu numeerinen painoarvo (0 = seinä)
* kerrottu korkeus ja leveys
* kerrottu milloin kartta alkaa

```
# esimerkki sisällöstä
decode-begin
T 0
. 1
decode-end

height 2
width 3
map
T.T
T.T
```

#### 1.2 Kartan luominen
* Heti generaattorin valinna jälkeen generaattori kysyy lisä tietoja mm. lukija listaa kartat maps kansiosta ja kysyy mitä käytetään
* Kyselyn jälkeen luodaan kartta

### 2 Valitse ajettava algoritmi

#### 2.1.success Valitaan listasta algoritmeja testattava
#### 2.1.fail Jos karttaa ei ole asetettu palaudutaan päävalikkoon, missä voidaan toteuttaa askel 1
#### 2.2 Algoritmi saattaa kysyä mitä tietorakenteita tulisi käyttää jos siitä on enemmän variaatioita
#### 2.3 Siirtyy suoraan askel 3:een

### 3 ??????
Tässä taika-askeleessa algoritmi käy haunsa läpi ja kirjoittaa tuloksensa juurikansion viereen muodossa 
doc/reports/algoritmin_nimi/kartan_nimi/aika_leima.tiedostopääteTBD. Vielä määrittämättä raportin sisältö.  
#### Muistilista tarvittavasta siällöstä
* Miten kirjoitetaan koneen specsit raporttiin
* Kovakoodata vai lukea teoreettiset tila-, aikavaativuudet jostain
* Tiedostotyyppivaihtoehtoina html, pdf, md
* Piirtää tai linkittää kartan
* Kirjoittaa ajan
* Tilan käytön selvitys?

### 4 Profit
Palataan päävalikkoon. Voit ihailla tai analysoida tuloksia.