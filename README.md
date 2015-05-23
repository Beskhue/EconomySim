# EconomySim
This is a [craft]bukkit/spigot plugin for simulating a Minecraft economy. It works by keeping track of the amount of items bought and sold to shops.

### Algorithm
To simulate prices changing as a result of the market, while being completely agnostic of actual block values (which might vary from server-to-server), the price of items is based on their demand. The demand is calculated as `totalBought - totalSold` per item (a negative demand is possible). The price is calculated from this value. For the default equation parameters, the price vs. demand curve can be seen below. Notably, the price drops rapidly as people sell them, which generally is as desired: items like dirt are available in abundance. When items are more in demand than in supply, their prices increase rapidly.

![Price Curve](https://i.imgur.com/T72EzUB.png)

##### Decay
To help numerical stability, especially in small economies, a decay on the transaction amounts is used. A configurable percentage of transaction amounts will be subtracted each day (simulated at a shorter time-interval). The decay percentages are settable seperately for sale and buy amounts. For example, take the sale decay at 1%/day and the buy decay at 10%/day. If at the start of the day the sale and buy amounts for an item are 1000 and 200 respectively, they will be 990 and 180 at the end of the day.

### Citizens 2.0
EconomySim is shipped with [Citizens](http://wiki.citizensnpcs.co/Citizens_Wiki) support. To use EconomySim with Citizens, give a Citizens NPC the EconomySim trait and then use the _esSetShop_-command on the NPC to set its shop.

### Documentation
The Javadoc for EconomySim can be found [here](https://beskhue.github.io/EconomySim).
