# Coin-Address-Generator

https://github.com/MrMaxweII/Bitcoin-Address-Generator/releases/download/V3.1.1/CoinAddressGeneratorV3.1.1.jar

Hash SHA-256  "CoinAddressGeneratorV3.1.1.jar"   =   8513aea9c3826bdd73fff6efd6d7c11bf2f6b48a6064c03a0cfbca39ae41e3fa



![CoinAddressGenV3](https://user-images.githubusercontent.com/34688939/75117189-a18dba00-566f-11ea-9ff1-2c1db29ded1a.png)



The Coin-AddressGenerator creates a private key in Bitcoin Wallet Import Format WIF,
as well as the associated coin address and public key.
A paper wallet with QR code can be created and printed out.

    
#### This is a Java application, so you need to install Java!
https://java.com/de/download/


#### Start the program
The "Coin_Address_Generator.jar" file is located in the "release" folder.
You can start this under Windows if Java is installed simply by double-clicking.
On Linux, type in the console: java -jar CoinAddressGeneratorV3.1.0.jar



#### Compile code
The project was created with eclipse.
You can either import it back into eclipse or use the java source files.
All necessary source files are in the src folder.
To import to eclipse go to File / Open Projects from File System, select the ZIP archive, finish!
All required libraries are already included in the project (in the lib folder). So you can start the project directly.



#### Multicoin 
There is a large list of coins to choose from that can be imported.
However, some coins may not work properly because the coin parameter list is quickly out of date and cannot be kept up to date.
It is the responsibility of each user to check that the keys created are correct! Use of this software is at your own risk!



#### Input:
There are three ways to create the private key:

1. Entry as text:
Any text can be entered.
This text then becomes the private key with a hash,
Public key and the Bitcoin address generated.

2. cube sign:                                  
There can be 100 dice characters in Base6 (also characters between 1 and 6).
This includes: 1=1, 2=2, 3=3, 4=4, 5=5, 6=0.

3. The private key can also be entered directly in all common formats:
Hexa, Base58, Base58 compressed and Base64.
Checksum check is implemented.



#### output
- The format of the generated keys and addresses can be set under "Settings"
- the public key is given in hexa
- The coin address can be output in WIF-uncompressed, WIF-compressed, P2SH and Bech32
- The QR code of the private key and the coin address is displayed



#### Issue of the coin amount
- If the internet connection is active, the coin amount belonging to the key is displayed
- The amount is queried on a suitable website
- If no internet connection is available, nothing is displayed.



#### QR code check
For safety, the program reads the QR code back in as an image,
scanned and checked.
This prevents an incorrect QR code from being displayed.



#### Save and open the wallet

An encrypted wallet with any number of keys can be saved.
A strong password must be entered for this.
To increase security, encryption is carried out in succession using AES and Twofish.
In addition, the encryption contains a certain brute force protection, which extends the runtime with the help of a scrypt hash.



#### Create a paper wallet.
The surface of the program can be printed out or saved as an image.



#### Contact
If you find bugs, ideas for improvements, or just have questions,
I am happy about every mail: Maxwell-KSP@gmx.de


#### donate
If you like the Coin Address Generator, I would be very happy to receive a donation: 
#### 12zeCvN7zbAi3JDQhC8tU3DBm35kDEUNiB 
   



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





