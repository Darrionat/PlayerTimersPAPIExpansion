package me.darrionat;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.darrionat.playertimers.PlayerTimer;
import me.darrionat.playertimers.PlayerTimersPlugin;

public class PlayerTimersExpansion extends PlaceholderExpansion {

	private PlayerTimersPlugin plugin;

	@Override
	public String getIdentifier() {
		return "playerTimers";
	}

	@Override
	public String getAuthor() {
		return "Darrionat";
	}

	@Override
	public String getVersion() {
		return "1.1.0";
	}

	/**
	 * Returns the name of the required plugin.
	 *
	 * @return {@code DeluxeTags} as String
	 */
	@Override
	public String getRequiredPlugin() {
		return "PlayerTimers";
	}

	/**
	 * Used to check if the expansion is able to register.
	 * 
	 * @return true or false depending on if the required plugin is installed
	 */
	@Override
	public boolean canRegister() {
		if (!Bukkit.getPluginManager().isPluginEnabled(getRequiredPlugin())) {
			return false;
		}
		plugin = (PlayerTimersPlugin) Bukkit.getPluginManager().getPlugin(getRequiredPlugin());
		return plugin != null;
	}

	/**
	 * This method is called when a placeholder is used and matches the set
	 * {@link #getIdentifier() identifier}
	 *
	 * @param offlinePlayer The player to parse placeholders for
	 * @param params        The part after the identifier
	 *                      ({@code %identifier_params%})
	 *
	 * @return returns value or possibly {@code null}
	 */
	@Override
	public String onRequest(OfflinePlayer offlinePlayer, String params) {
		int id;
		int place;
		String[] args = params.split("_");
		try {
			id = Integer.parseInt(args[0]);
			place = Integer.parseInt(args[1]);
		} catch (NumberFormatException exe) {
			exe.printStackTrace();
			return "Invalid Placeholder";
		} catch (ArrayIndexOutOfBoundsException exe) {
			exe.printStackTrace();
			return "Not enough arguments";
		}

		List<PlayerTimer> sortedTimes = getSortedExistingTimes(id);

		if (sortedTimes.size() >= place && place > 0) {
			PlayerTimer time = sortedTimes.get(place - 1);
			OfflinePlayer player = Bukkit.getOfflinePlayer(time.getPlayer());

			String durationStr = getDurationString(time.getDuration());

			return String.valueOf(place) + " " + player.getName() + ": " + durationStr;
		}

		return null;
	}

	/**
	 * Gets and sorts the existing times from shortest duration to most
	 * 
	 * @param id the id of the timer to sort for
	 * @return returns a list of {@code PlayerTimer} sorted from shortest duration
	 *         to longest
	 */
	public List<PlayerTimer> getSortedExistingTimes(int id) {
		List<PlayerTimer> list = plugin.getTimesRepository().getSavedTimes(id);

		// Sort the list
		Collections.sort(list, new Comparator<PlayerTimer>() {
			public int compare(PlayerTimer t1, PlayerTimer t2) {
				Long duration = t1.getDuration();
				return duration.compareTo(t2.getDuration());
			}
		});
		return list;
	}

	public static String getDurationString(long millis) {
		long totalSeconds = millis / 1000;
		double days = Math.floor(totalSeconds / 86400);
		double hours = Math.floor(totalSeconds % 86400 / 3600);
		double minutes = Math.floor(totalSeconds % 3600 / 60);
		double seconds = Math.floor(totalSeconds % 60);

		String duration = days + "d " + hours + "h " + minutes + "m " + seconds + "s";

		if (days < 1) {
			duration = duration.replace(days + "d ", "");
			if (hours < 1) {
				duration = duration.replace(hours + "h ", "");
				if (minutes < 1) {
					duration = duration.replace(minutes + "m ", "");
				}
			}
		}
		return duration;
	}
}