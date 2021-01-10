package code.jasgo.skin.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.URL;
import java.nio.charset.Charset;

import javax.net.ssl.HttpsURLConnection;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_16_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import net.minecraft.server.v1_16_R1.EntityPlayer;
import net.minecraft.server.v1_16_R1.PacketPlayOutPlayerInfo;

public class SkinGrabber {

	public static void changeSkin(Player player, String name) {
		try {
			GameProfile profile = ((CraftPlayer) player).getHandle().getProfile();

			profile.getProperties().removeAll("textures");
			profile.getProperties().put("textures", getSkin(name));

			reloadPlayer(player);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static Property getSkin(String name) throws IOException, ParseException {
		JSONParser parser = new JSONParser();
		Object obj;
		JSONObject json;
		Object obj1;
		JSONObject json1;
		JSONObject raw;
		JSONArray arr;
		JSONObject skin = new JSONObject();
		obj = parser.parse(getUUID(name));
		json = (JSONObject) obj;
		obj1 = parser.parse(getProfile((String) json.get("id")));
		json1 = (JSONObject) obj1;
		raw = (JSONObject) json1.get("raw");
		arr = (JSONArray) raw.get("properties");
		skin = (JSONObject) arr.get(0);
		return new Property("textures", (String) skin.get("value"), (String) skin.get("signature"));
	}

	public static String getUUID(String playername) throws IOException {
		URL url = new URL("https://api.minetools.eu/uuid/" + playername);
		HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
		connection.setDoOutput(true);
		connection.setRequestMethod("GET");
		StringBuilder response = new StringBuilder();
		BufferedReader in = new BufferedReader(
				new InputStreamReader((InputStream) connection.getContent(), Charset.forName("UTF-8")));
		connection = (HttpsURLConnection) url.openConnection();
		String line;
		while ((line = in.readLine()) != null) {
			response.append(line);
		}
		in.close();
		return response.toString();
	}

	public static String getProfile(String uuid) throws IOException {
		URL url = new URL("https://api.minetools.eu/profile/" + uuid);
		HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
		connection.setDoOutput(true);
		connection.setRequestMethod("GET");
		StringBuilder response = new StringBuilder();
		BufferedReader in = new BufferedReader(
				new InputStreamReader((InputStream) connection.getContent(), Charset.forName("UTF-8")));
		String line;
		while ((line = in.readLine()) != null) {
			response.append(line);
		}
		in.close();
		return response.toString();
	}

	@SuppressWarnings("deprecation")
	public static void reloadPlayer(Player p) {
		Bukkit.getOnlinePlayers().forEach(pl -> {
			((CraftPlayer) pl).getHandle().playerConnection.sendPacket(new PacketPlayOutPlayerInfo(
					PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, ((CraftPlayer) pl).getHandle()));
		});
		Bukkit.getOnlinePlayers().forEach(pl -> {
			((CraftPlayer) pl).getHandle().playerConnection.sendPacket(new PacketPlayOutPlayerInfo(
					PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, ((CraftPlayer) pl).getHandle()));
		});

		Bukkit.getOnlinePlayers().forEach(pl -> pl.hidePlayer(p));
		Bukkit.getOnlinePlayers().forEach(pl -> pl.showPlayer(p));
		World nether = Bukkit.getWorld("world_nether");
		Location loc1 = new Location(nether, 0, 300, 0);
		Location loc2 = new Location(p.getWorld(), p.getLocation().getX(), p.getLocation().getY(),
				p.getLocation().getZ(), p.getLocation().getYaw(), p.getLocation().getPitch());
		p.teleport(loc1);
		p.teleport(loc2);
	}

	public static void setPlayerNameTag(Player p, String name) {
		try {
			EntityPlayer enp = ((CraftPlayer) p).getHandle();

			Object obj = enp.getClass().getMethod("getProfile").invoke(enp);
			Field nameField = obj.getClass().getDeclaredField("name");
			nameField.setAccessible(true);
			nameField.set(obj, name);

			reloadPlayer(p);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
