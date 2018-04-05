package net.grydeske

import com.datastax.driver.core.Session
import com.datastax.driver.core.utils.UUIDs

import net.grydeske.Repositories.KeyspaceRepository
import net.grydeske.Repositories.PokemonRepository
import net.grydeske.domain.Pokemon

class SampleApplication {

    void runApplication() {
        CassandraConnector connector = new CassandraConnector()
        connector.connect("127.0.0.1", null)
        Session session = connector.getSession()

        KeyspaceRepository sr = new KeyspaceRepository(session: session)
        PokemonRepository pokemonRepository = new PokemonRepository(session: session)

        Scanner sc = new Scanner(System.in)

        printHeader("What do you want to do?")

        String line = sc.nextLine()
        while( line != 'exit' ) {
            switch (line) {
                case 'create':
                    println "Creating keyspace and tables"
                    sr.createKeyspace("pokemon", "SimpleStrategy", 1)
                    sr.useKeyspace("pokemon")
                    pokemonRepository.createTable()
                    pokemonRepository.alterTablePokemons("total", "int")
                    pokemonRepository.createTablePokemonsByNumber()
                    println "Creating keyspace and tables - done"
                    break
                case 'load':
                    println "Loading pokemons"
                    def pokemons = readPokemonsFromFile()
                    pokemons.each { Pokemon pokemon ->
                        pokemonRepository.insertPokemonBatch(pokemon)
                    }
                    println "Loading pokemons - done"
                    break
                case 'id':
                    getByNumber(sc)
                    break
                case 'scan':
                    println "Scanning all"
                    pokemonRepository.selectAll().each{ Pokemon p -> println("Pokemon: ${p}") }
                    break
                case 'destroy':
                    println "Destroying keyspace"
                    sr.deleteKeyspace("pokemon")
                default:
                    println "Unknown input: ${line}"
            }
            line = sc.nextLine()
        }

        connector.close()

    }

    List<Pokemon> readPokemonsFromFile() {
        def pokemons = []
        def first = true
        def headers

        this.getClass().getResource( '/Pokemon.csv' ).text.eachLine {
            def parts = it.split(',')
            if( first) {
                headers = parts
                first = false
            } else {
                if( parts.size() == 13) {
                    pokemons << new Pokemon(
                            id: UUIDs.timeBased(),
                            number: parts[0] as Integer,
                            name: parts[1],
                            type1: parts[2],
                            type2: parts[3],
                            total: parts[4] as Integer,
                            generation: parts[11] as Integer,
                            legendary: parts[12] == 'True'
                    )
                }
            }
        }

        pokemons
    }

    void getByNumber(Scanner sc) {
        println "Which number should we look for?"
        String number = sc.nextLine()

        // TODO - You should do something here - maybe even have more inputs to this method
    }

    static void printHeader(String text) {
        println ""
        println "*"*80
        println text.center(80)
        println "*"*80
    }

}
