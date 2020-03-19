# Bitcoin-Address-Generator



Hash SHA-256  "CoinAddressGeneratorV3.1.0.jar"   =   5f5fa204b1d5505c3056a6432f39ad2628aff47ffcbc73494e566757bce08e60



![CoinAddressGenV3](https://user-images.githubusercontent.com/34688939/75117189-a18dba00-566f-11ea-9ff1-2c1db29ded1a.png)


##### ------------------------------ DEUTSCH ------------------------------

Der Coin-AddressGenerator erstellt einen privat Schlüssel im Bitcoin Wallet Import Format WIF,           
sowie die zugehörige Coin-Adresse und den öffentlichen Schlüssel.                                     
Es kann eine Paper-Wallet mit QR-Code erstellt und ausgedruckt werden.  
    
#### Dies ist eine Java Anwendung, sie müssen also Java installieren!
https://java.com/de/download/

#### Programm Starten
Im Ordner "release" befindet sich die "Coin_Address_Generator.jar" Datei.                        
Diese können sie, wenn Java installiert ist einfach per Doppelklick Starten.



#### Code kompilieren
Das Projekt ist mit eclipse erstellt worden.
Sie können es entweder in eclipse wieder importieren oder die java Quelldateien verwenden.
Es befinden sich alle benötigten Quelldateien im src Ordner.
Zum Import nach eclipse gehen sie auf Datei/Open Projects from File System, wählen sie das ZIP Archiv aus, Fertigstellen!
Alle benötigten Bibliotheken, sind bereits im Projekt enthalten (im lib Ordner). Sie können also das Projekt direkt starten.



#### Multicoin 
Es steht eine große Liste an Coins zur Auswahl die importiert werden können.
Möglicherweise funktionieren aber einige Coins nicht ordnungsgemäß, da die Coin-Parameter-Liste schnell veraltet ist und nicht aktuell gehalten werden kann.
Es liegt in der eigenen Verantwortung eines jeden Benutzers, die Korrektheit der erstellten Keys zu prüfen! Die Benutzung dieser Software geschieht auf eigene Verantwortung!



#### Eingabe:
Zum Erstellen des Private Key´s gibt es drei Möglichkeiten:

1. Eingabe als Text:                                    
Es kann ein beliebiger Text eingegeben werden. 
Aus diesem Text wird dann durch einen Hash der Private Key,                 
Public Key und die Bitcoin Adresse erzeugt.

2. Würfelzeichen:                                   
Es können 100 Würfelzeichen in Base6 (also Zeichen zwischen 1 und 6) eingegeben werden.
Dabei entspricht: 1=1, 2=2, 3=3, 4=4, 5=5, 6=0.

3. Der Private Key kann aber auch direkt in allen üblichen Formaten eingegeben werden:
Hexa, Base58, Base58 compressed und Base64.
Checksumme Prüfung ist implementiert.



#### Ausgabe
- Das Format der erzeugten Keys und Adressen kann unter "Settings" eingestellt werden
- der öffentliche Schlüssel wird in Hexa ausgegeben 
- Die Coin Adresse kann in WIF-uncompressed, WIF-compressed, P2SH und Bech32 ausgegeben werden
- Der QR-Code des Private Keys und der Coin-Adresse wird angezeigt



#### Ausgabe des Coin Betrages
- Bei aktiver Internetverbindung wird der Coin Betrag der zu dem Key gehört angezeigt
- Dazu wird der Betrag auf einer geeigneten Internetseite abgefragt
- Ist keine Internetverbindung verfügbar, wird nichts angezeigt.



#### QR-Code Prüfung
Zur Sicherheit wird der QR-Code vom Programm selbst als Bild wieder eingelesen, 
gescannt und geprüft.                       
Damit wird verhindert, dass ein falscher QR-Code angezeigt werden könnte.



#### Paper Wallet erstellen.
Die Oberfläche des Programms kann als Bild ausgedruckt oder gespeichert werden. 



#### Kontakt
Wenn sie Bug´s finden, Ideen für Verbesserungen, oder einfach Fragen haben,                 
freue ich mich über jede Mail: Maxwell-KSP@gmx.de


#### Spenden
Wenn Ihnen der Bitcoin Adress Generator gefällt, würde ich mich über eine Spende sehr freuen: 
#### 12zeCvN7zbAi3JDQhC8tU3DBm35kDEUNiB 
   


#### Haftungsausschluss

DIESE SOFTWARE WIRD VON DEN COPYRIGHT-INHABERN UND -BETREIBERN "WIE GESEHEN" ZUR VERFÜGUNG GESTELLT. 
JEGLICHE AUSDRÜCKLICHE ODER IMPLIZITE GEWÄHRLEISTUNG, EINSCHLIESSLICH, 
ABER NICHT BESCHRÄNKT AUF DIE STILLSCHWEIGENDEN GEWÄHRLEISTUNGEN DER MARKTGÄNGIGKEIT 
UND DER EIGNUNG FÜR EINEN BESTIMMTEN ZWECK, WIRD ABGELEHNT. 
IN KEINEM FALL HAFTEN DER COPYRIGHT-INHABER ODER DIE BEITRÄGER FÜR DIREKTE, 
INDIREKTE, ZUFÄLLIGE, SPEZIELLE, EXEMPLARISCHE ODER FOLGESCHÄDEN (EINSCHLIESSLICH, 
ABER NICHT BESCHRÄNKT AUF, BESCHAFFUNG VON ERSATZGÜTERN ODER DIENSTLEISTUNGEN; 
VERLUST VON VERWENDUNG, DATEN ODER GEWINNEN; ODER GESCHÄFTSUNTERBRECHUNG) JEDOCH AUFGRUND DER HAFTUNGSFREISTELLUNG, 
AUCH IM VERTRAG, AUSSCHLIESSLICHER HAFTUNG ODER TATSACHE (EINSCHLIESSLICH FAHRLÄSSIGKEIT ODER ANDERWEITIG), 
DIE SICH AUS DER NUTZUNG DIESER SOFTWARE ERGEBEN, SELBST WENN SIE AUF DIE MÖGLICHKEIT SOLCHER SCHÄDEN BERATEN WIRD.






#### Disclaimer
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
POSSIBILITY OF SUCH DAMAGE.





