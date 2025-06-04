
# ZenithMC Circle Game Plugin

Minecraft sunucusu için Circle Game battle royale plugin'i.

## Özellikler

- 9 takım, her takımda 2 oyuncu (toplam 18 oyuncu)
- Her turda ortaya rastgele loot çıkar
- 30 saniyelik loot toplama süresi
- Eleme sistemi ile son 2 takım kalana kadar devam eder
- Final 1v1 savaşı
- Kazanan takım ödüllendirilir

## Kurulum

1. `mvn clean package` ile plugin'i derleyin
2. Oluşan .jar dosyasını sunucunuzun `plugins` klasörüne koyun
3. Sunucuyu yeniden başlatın

## Komutlar

- `/circlegame create` - Yeni oyun oluştur
- `/circlegame join <oyuncu>` - Oyuna katıl
- `/circlegame start` - Oyunu başlat
- `/circlegame help` - Yardım menüsü

## Kullanım

1. Bir oyuncu `/circlegame create` ile oyun oluşturur
2. Diğer oyuncular `/circlegame join <oluşturan_oyuncu>` ile katılır
3. En az 6 oyuncu toplandığında `/circlegame start` ile oyun başlar
4. Oyuncular otomatik olarak takımlara dağıtılır
5. Her turda loot için koşma zamanı başlar
6. Belirli aralıklarla takımlar elenir
7. Son 2 takım final savaşı yapar

## Gereksinimler

- Spigot/Paper 1.19+
- Java 8+
