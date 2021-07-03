# Creatures' natural selection simulation

### Description
This pet project was created to practise OOP and multithreading programming. Moreover, I was interested how creatures will live and struggle for food in habitat that I defined.
By the way, Quadrant is an imaginary creature that will participate in the simulation.

### Simulation rules:
   - if the quadrant hasn't eaten any food-units during the simulation cycle
     it will stop participating in the simulation.
   - if the quadrant has eaten ONE food-unit it will continue surviving in
     the next round.
   - if the quadrant has eaten at least TWO food-units it will continue
     surviving in the next round and will give a descendant (потомок).
     (No difference has many food-units the quadrant has eaten he will
     give only one descendant).
 
 Each instance of Quadrant class will run as a unique thread.
 
 ### An example simulation test:
 The simulation was carried out on a map 25x25 units. The initial number of creatures was 130. On the seconds day, the number of creatures was reduced to 57. I think this was due to the lack of food on the map. During the next 2 rounds the number of creatures increased to ~95. Thereafter, the number of creatures fluctuated between 85 and 95. It occured that the ideal number of inhabitants on a map 25x25 with a given number of food was ~90. I can conclude, that the natural selection itselfs determined the ideal number of creatures in these conditions. So the original number 130 creatures that I initialy selected was too large for these conditions. 
 
 ![alt text](https://lh3.googleusercontent.com/nFGRFenpO82BUco1s0ZZVHwrJ9RMaTPKQdIMktOHK-J6woQcsskkpF0lScvw70SoxcVqQscmArFp2RyN11Rbls3yEKe_EuiIu6QDALDyCgfFJ53gzYb9DgwINXdOaEqpXaKOeowy9mk2GweBwDE5MIMEjsdRf_6X6ZXlArFN9Ax9agtGfQMO4UJO9AeE-v9BfwWoU-Qtq0VVVraFlStjevOsLGBKCliuIOSl_pjiBgACw_8PFWldcoVOv3XKG9TYHDUzhDwMUjfCr--sLZ4_FYT8JM-ZZblmGv7yqmgF1_RA3HVOs07KmAEvSlN9NO-vV5MKrgRmgoCnyQdR1Rfl0v9K_jfvMrmknJBT67fOHeXHkpXvls-l-nQZg_IIQKqnTOcYMWlPVcKOOiCXrfXv3ukuQR3OPoxF1gnL-4WI9uelpAplCvLCGbutq4E7ed3NOdysmOEmk4K34au4xsW-HrOJCrwFZFQMeoOp0Z6rXn8St4H-o9dBb7GBv3U_BD5K2qmHqpbn3F3CQh-yvaM4pFn5dgducIkf-9WAJydWK0BUSZSLVJKmrnW0l5DBPbwqBi4ilO4iWlWgVsTltBIhpbc_fQP48L6RD2sV06bNQb_W94S7DfkV_nR5jfRHiwnEjjxZHD4Bo1Ap7jTwStW2qo03LpX_Fxj1ao_Xb2kdtYi9GXpiSG_fu_ExlP9rus5wHjxywmdaQgfJ8LpM0nBe9Ow=w959-h620-no?authuser=0)
