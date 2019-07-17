# Simulation Specification

## Demo
<img src="demo/demo.gif" data-canonical-src="" width="600"  />

## TODO
[https://docs.google.com/document/d/1ZiuWXCt2R6VJhi4gJ9Ioh0zyY4dZL7d4ZuxnsBEnp2w/edit?usp=sharing](https://docs.google.com/document/d/1ZiuWXCt2R6VJhi4gJ9Ioh0zyY4dZL7d4ZuxnsBEnp2w/edit?usp=sharing)

## Description

## Simulation Rules

## Collision Rules 
Should be fair (point's order in list shouldn't affect outcome)

1. On simulation step: if 2 adjacent points, BOTH points cannot move to the direction with an adjacent point

These actions are only allowed when the simulation is reset:

2. Adding a walker: spiral around origin until a free spot is found
3. Resetting walkers' position: place walkers in a spiral around origin
4. Toggling collisions on: turning this on should reset the walkers (above) 
