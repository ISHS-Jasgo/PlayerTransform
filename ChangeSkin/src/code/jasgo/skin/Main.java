package code.jasgo.skin;

import java.util.HashMap;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import code.jasgo.skin.utils.SkinGrabber;

public class Main extends JavaPlugin implements Listener {

	HashMap<String, String> hash = new HashMap<String, String>();

	@Override
	public void onEnable() {
		System.out.println("SkinPlugin Enabled");
		this.getServer().getPluginManager().registerEvents(this, this);
	}

	@Override
	public void onDisable() {
		System.out.println("SkinPlugin Disabled");
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (label.equalsIgnoreCase("skin")) {
			Player player = (Player) sender;
			if (hash.get(player.getUniqueId().toString()) != null) {
				SkinGrabber.changeSkin(player, args[0]);
				SkinGrabber.setPlayerNameTag(player, args[0]);
				player.setDisplayName(args[0]);
				hash.replace(player.getUniqueId().toString(), args[0]);
			} else {
				SkinGrabber.changeSkin(player, args[0]);
				SkinGrabber.setPlayerNameTag(player, args[0]);
				player.setDisplayName(args[0]);
				hash.put(player.getUniqueId().toString(), args[0]);
			}

		}
		return false;
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		if (hash.get(event.getPlayer().getUniqueId().toString()) != null) {
			String name = hash.get(event.getPlayer().getUniqueId().toString());
			SkinGrabber.changeSkin(event.getPlayer(), name);
			SkinGrabber.setPlayerNameTag(event.getPlayer(), name);
			event.getPlayer().setDisplayName(name);
		}
	}
}
