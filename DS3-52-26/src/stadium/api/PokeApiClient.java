package stadium.api;

import org.json.JSONArray;
import org.json.JSONObject;
import stadium.modelo.Pokemon;

import javax.imageio.ImageIO;
import java.awt.Image;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

//Se encarga de hablar con PokeAPI y convertir el JSON en un objeto Pokemon
public class PokeApiClient
{
    private static final String URL_BASE = "https://pokeapi.co/api/v2/pokemon/";

    //cantidad de pokemon que existen en la API (ids del 1 al 1025)
    private static final int TOTAL_POKEMON = 1025;

    private final HttpClient client = HttpClient.newHttpClient();
    private final Random random = new Random();

    //busca un pokemon al azar usando un id aleatorio
    public Pokemon buscarPokemonAleatorio() throws Exception
    {
        int id = random.nextInt(TOTAL_POKEMON) + 1;
        return buscarPokemon(String.valueOf(id));
    }

    //busca un pokemon por nombre (la API tambien acepta el id)
    public Pokemon buscarPokemon(String nombre) throws Exception
    {
        //la API solo entiende nombres en minuscula y sin espacios
        nombre = nombre.trim().toLowerCase().replace(" ", "-");

        if (nombre.isEmpty())
        {
            throw new Exception("Escribe el nombre de un pokemon");
        }

        HttpResponse<String> response;

        try
        {
            //se crea un objeto de tipo request para realizar la peticion
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(URL_BASE + nombre))
                    .timeout(Duration.ofSeconds(10))
                    .build();

            //ejecutamos la solicitud
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }
        catch (IllegalArgumentException e)
        {
            //pasa cuando el nombre tiene caracteres que no sirven en una URL
            throw new Exception("Pokemon no encontrado: " + nombre);
        }
        catch (IOException e)
        {
            throw new Exception("Error de red, revisa tu conexion a internet");
        }

        //404 es la respuesta de la API cuando el pokemon no existe
        if (response.statusCode() == 404)
        {
            throw new Exception("Pokemon no encontrado: " + nombre);
        }

        if (response.statusCode() != 200)
        {
            throw new Exception("Error de la API (codigo " + response.statusCode() + ")");
        }

        return convertirJson(response.body());
    }

    //descarga la imagen del pokemon, devuelve null si no tiene sprite
    public Image descargarSprite(Pokemon pokemon) throws Exception
    {
        if (pokemon.getSpriteUrl() == null)
        {
            return null;
        }

        try
        {
            return ImageIO.read(URI.create(pokemon.getSpriteUrl()).toURL());
        }
        catch (IOException e)
        {
            throw new Exception("Error de red al descargar la imagen");
        }
    }

    //saca del JSON solo los datos que necesita el combate
    private Pokemon convertirJson(String body)
    {
        //Creamos el objeto JSON
        JSONObject json = new JSONObject(body);

        String nombre = json.getString("name");

        //Accedemos al array de tipos, cada elemento tiene adentro un objeto "type"
        List<String> tipos = new ArrayList<>();
        JSONArray tiposJson = json.getJSONArray("types");
        for (int i = 0; i < tiposJson.length(); i++)
        {
            JSONObject tipoJson = tiposJson.getJSONObject(i).getJSONObject("type");
            tipos.add(tipoJson.getString("name"));
        }

        //Accedemos al array de estadisticas y nos quedamos con las 4 del taller
        int hp = 0;
        int attack = 0;
        int defense = 0;
        int speed = 0;

        JSONArray statsJson = json.getJSONArray("stats");
        for (int i = 0; i < statsJson.length(); i++)
        {
            JSONObject statJson = statsJson.getJSONObject(i);
            String nombreStat = statJson.getJSONObject("stat").getString("name");
            int valor = statJson.getInt("base_stat");

            if (nombreStat.equals("hp"))
            {
                hp = valor;
            }
            else if (nombreStat.equals("attack"))
            {
                attack = valor;
            }
            else if (nombreStat.equals("defense"))
            {
                defense = valor;
            }
            else if (nombreStat.equals("speed"))
            {
                speed = valor;
            }
        }

        //accedemos al objeto de imagen, algunos pokemon no tienen sprite frontal
        JSONObject spritesJson = json.getJSONObject("sprites");
        String spriteUrl = null;
        if (!spritesJson.isNull("front_default"))
        {
            spriteUrl = spritesJson.getString("front_default");
        }

        return new Pokemon(nombre, tipos, spriteUrl, hp, attack, defense, speed);
    }
}
