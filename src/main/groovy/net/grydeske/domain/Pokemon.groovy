package net.grydeske.domain

import groovy.transform.ToString

@ToString(includeNames = true)
class Pokemon {

    UUID id
    Integer number
    String name
    String type1
    String type2
    Integer total
    Integer hp
    Integer attack
    Integer Ddfense
    Integer spAtk
    Integer spDef
    Integer speed
    Integer generation
    Boolean legendary

}
