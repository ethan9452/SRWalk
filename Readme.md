# Simulation Specification

## Demo
<img src="demo/demo.gif" data-canonical-src="" width="600"  />

## TODO
[https://docs.google.com/document/d/1ZiuWXCt2R6VJhi4gJ9Ioh0zyY4dZL7d4ZuxnsBEnp2w/edit?usp=sharing](https://docs.google.com/document/d/1ZiuWXCt2R6VJhi4gJ9Ioh0zyY4dZL7d4ZuxnsBEnp2w/edit?usp=sharing)

## Description
Simulation of different objects in a X-Y plane

## Simulation Objects
- Walker: every step, randomly moves 1 space up, down, left, or right (not all moves are always possible, explained below)
- Wall: walkers can't move into a space with a wall
- Magnet: "attratcs" or "repels" walkers by altering the probability dist of the walker's possilbe moves


## Simulation Rules

## Object Interactions 
Should be fair (point's order in list shouldn't affect outcome)

|                              | Walker                      | Wall                      | Magnet                      |
|-----------------------------|-----------------------------|---------------------------|---------------------------  |
|     Walker                         | If collisions on, cannot move to a spot where another walker currently is | Can never intersect  | Magnet attracts or repels walkers |
|   Wall                           |   xxx                    |  Nothing                     | Nothing                      |
|    Magnet                          |    xxx                   |    xxx                   |     Nothing                  |


