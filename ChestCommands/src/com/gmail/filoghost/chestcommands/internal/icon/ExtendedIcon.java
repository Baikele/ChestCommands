package com.gmail.filoghost.chestcommands.internal.icon;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.gmail.filoghost.chestcommands.ChestCommands;
import com.gmail.filoghost.chestcommands.Permissions;
import com.gmail.filoghost.chestcommands.api.Icon;
import com.gmail.filoghost.chestcommands.bridge.EconomyBridge;
import com.gmail.filoghost.chestcommands.bridge.PlayerPointsBridge;
import com.gmail.filoghost.chestcommands.internal.RequiredItem;
import com.gmail.filoghost.chestcommands.util.Utils;

public class ExtendedIcon extends Icon {

	private String permission;
	private String permissionMessage;
	private double moneyPrice;
	private int playerPointsPrice;
	private RequiredItem requiredItem;
	
	public ExtendedIcon() {
		super();
	}

	public String getPermission() {
		return permission;
	}

	public void setPermission(String permission) {
		this.permission = permission;
	}

	public String getPermissionMessage() {
		return permissionMessage;
	}

	public void setPermissionMessage(String permissionMessage) {
		this.permissionMessage = permissionMessage;
	}
	
	public double getMoneyPrice() {
		return moneyPrice;
	}

	public void setMoneyPrice(double moneyPrice) {
		this.moneyPrice = moneyPrice;
	}

	public int getPlayerPointsPrice() {
		return playerPointsPrice;
	}

	public void setPlayerPointsPrice(int playerPointsPrice) {
		this.playerPointsPrice = playerPointsPrice;
	}

	public RequiredItem getRequiredItem() {
		return requiredItem;
	}

	public void setRequiredItem(RequiredItem requiredItem) {
		this.requiredItem = requiredItem;
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean onClick(Player player) {
		
		// Check all the requirements.
		
		if (permission != null && !permission.isEmpty() && !player.hasPermission(permission)) {
			if (permissionMessage != null) {
				player.sendMessage(permissionMessage);
			} else {
				player.sendMessage(ChatColor.RED + "You don't have permission.");
			}
			return closeOnClick;
		}
		
		if (moneyPrice > 0) {
			if (!EconomyBridge.hasValidEconomy()) {
				player.sendMessage(ChatColor.RED + "This command has a price, but Vault with a compatible economy plugin was not found. For security, the command has been blocked. Please inform the staff.");
				return closeOnClick;
			}
			
			if (!player.hasPermission(Permissions.BYPASS_ECONOMY) && !EconomyBridge.hasMoney(player, moneyPrice)) {
				player.sendMessage(ChestCommands.getLang().no_money.replace("{money}", EconomyBridge.formatMoney(moneyPrice)));
				return closeOnClick;
			}
		}
		
		if (playerPointsPrice > 0) {
			if (!PlayerPointsBridge.hasValidPlugin()) {
				player.sendMessage(ChatColor.RED + "This command has a price in points, but the plugin PlayerPoints was not found. For security, the command has been blocked. Please inform the staff.");
				return closeOnClick;
			}
			
			if (!PlayerPointsBridge.hasPoints(player, playerPointsPrice)) {
				player.sendMessage(ChestCommands.getLang().no_points.replace("{points}", Integer.toString(playerPointsPrice)));
				return closeOnClick;
			}
		}
		
		if (requiredItem != null) {
			
			if (!requiredItem.hasItem(player)) {
				player.sendMessage(ChestCommands.getLang().no_required_item
						.replace("{material}", Utils.formatMaterial(requiredItem.getMaterial()))
						.replace("{id}", Integer.toString(requiredItem.getMaterial().getId()))
						.replace("{amount}", Integer.toString(requiredItem.getAmount()))
						.replace("{datavalue}", requiredItem.hasRestrictiveDataValue() ? Short.toString(requiredItem.getDataValue()) : ChestCommands.getLang().any)
				);
				return closeOnClick;
			}
		}
		
		// Take the money, the points and the required item.
		
		if (moneyPrice > 0) {
			if (!player.hasPermission(Permissions.BYPASS_ECONOMY) && !EconomyBridge.takeMoney(player, moneyPrice)) {
				player.sendMessage(ChatColor.RED + "Error: the transaction couldn't be executed. Please inform the staff.");
				return closeOnClick;
			}
		}
		
		if (playerPointsPrice > 0) {
			if (!PlayerPointsBridge.takePoints(player, playerPointsPrice)) {
				player.sendMessage(ChatColor.RED + "Error: the transaction couldn't be executed. Please inform the staff.");
				return closeOnClick;
			}
		}
		
		if (requiredItem != null) {
			requiredItem.takeItem(player);
		}
		
		return super.onClick(player);
	}
	
	
}