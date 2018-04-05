package net.grydeske.Repositories

import com.datastax.driver.core.ResultSet
import com.datastax.driver.core.Row
import com.datastax.driver.core.Session
import net.grydeske.domain.Pokemon

class PokemonRepository {

    static final String TABLE_NAME = "pokemons"
    static final String TABLE_NAME_BY_NUMBER = TABLE_NAME + "ByNumber"

    Session session


    /**
     * Creates the pokemon table.
     */
    void createTable() {
        String query = "CREATE TABLE IF NOT EXISTS ${TABLE_NAME} (id uuid PRIMARY KEY, number int, name text, type1 text, type2 text, generation int, legendary boolean);"
        session.execute(query);
    }

    /**
     * Creates the pokemon by number table.
     */
    void createTablePokemonsByNumber() {
        String query = "CREATE TABLE IF NOT EXISTS ${TABLE_NAME_BY_NUMBER} (id uuid, number int, name text,PRIMARY KEY (number, id));"

        session.execute(query)
    }

    /**
     * Alters the table pokemons and adds an extra column.
     */
    void alterTablePokemons(String columnName, String columnType) {
        String query = "ALTER TABLE ${TABLE_NAME} ADD ${columnName} ${columnType};"

        session.execute(query)
    }

    /**
     * Insert a row in the table pokemons.
     *
     * @param pokemon
     */
    void insertpokemon(Pokemon pokemon) {
        String query = "INSERT INTO ${TABLE_NAME}(id, number, name, type1, type2, generation, legendary) VALUES (${pokemon.id}," +
                " '${pokemon.number}', '${pokemon.type1}', '${pokemon.type2}', '${pokemon.generation}', '${pokemon.legendary}');"

        session.execute(query);
    }

    /**
     * Insert a row in the table pokemonsbynumber.
     * @param pokemon
     */
    public void insertPokemonByNumber(Pokemon pokemon) {
        String query = "INSERT INTO ${TABLE_NAME_BY_NUMBER}(id, number, name) VALUES (${pokemon.id}, '${pokemon.number}', '${pokemon.name}');"

        session.execute(query);
    }

    /**
     * Insert a pokemon into two identical tables using a batch query.
     *
     * @param pokemon
     */
    public void insertPokemonBatch(Pokemon pokemon) {
        String query = "BEGIN BATCH INSERT INTO ${TABLE_NAME}(id, number, name, type1, type2, generation, legendary) VALUES (${pokemon.id},${pokemon.number},'${pokemon.name}','${pokemon.type1}','${pokemon.type2}',${pokemon.generation},${pokemon.legendary});" +
                "INSERT INTO ${TABLE_NAME_BY_NUMBER}(id, number, name) VALUES (${pokemon.id},${pokemon.number},'${pokemon.name}'); APPLY BATCH;"

        session.execute(query)
    }

    /**
     * Select pokemon by number.
     *
     * @return
     */
    public Pokemon selectByNumber(Integer number) {
        String query = "SELECT * FROM ${TABLE_NAME_BY_NUMBER} WHERE number = ${number};"


        ResultSet rs = session.execute(query)

        List<Pokemon> pokemons = []

        for (Row r : rs) {
            pokemons << new Pokemon(
                    id: r.getUUID("id"),
                    number: r.getInt('number'),
                    name: r.getString('name'),
                    type1: r.getString('type1'),
                    type2: r.getString('type2'),
                    total: r.getInt('total'),
                    generation: r.getInt('generation'),
                    legendary: r.getBool('legendary')
            )
        }

        pokemons.first()
    }

    /**
     * Select all pokemons from pokemons
     *
     * @return
     */
    public List<Pokemon> selectAll() {
        String query = "SELECT * FROM  ${TABLE_NAME}"

        ResultSet rs = session.execute(query);

        List<Pokemon> pokemons = []

        for (Row r : rs) {
            pokemons << new Pokemon(
                id: r.getUUID("id"),
                number: r.getInt('number'),
                name: r.getString('name'),
                type1: r.getString('type1'),
                type2: r.getString('type2'),
                total: r.getInt('total'),
                generation: r.getInt('generation'),
                legendary: r.getBool('legendary')
            )
        }
        return pokemons
    }

    /**
     * Select all pokemons from pokemonsbynumber
     * @return
     */
    List<Pokemon> selectAllPokemonByNumber() {
        String query = "SELECT * FROM ${TABLE_NAME_BY_NUMBER}"


        ResultSet rs = session.execute(query);

        List<Pokemon> pokemons = []

        for (Row r : rs) {
            pokemons << new Pokemon(
                    id: r.getUUID("id"),
                    number: r.getInt('number'),
                    name: r.getString('name')
            )
        }
        return pokemons
    }

    /**
     * Delete a pokemon by number.
     */
    public void deletepokemonByNumber(Integer number) {
        String query = "DELETE FROM ${TABLE_NAME_BY_NUMBER} WHERE number = ${number};"

        session.execute(query);
    }

    /**
     * Delete table.
     *
     * @param tableName the name of the table to delete.
     */
    public void deleteTable(String tableName) {
        String query = "DROP TABLE IF EXISTS ${tableName}"

        session.execute(query);
    }
}
