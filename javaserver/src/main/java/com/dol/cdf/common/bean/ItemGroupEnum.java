package com.dol.cdf.common.bean;

import io.nadron.app.Player;
import io.nadron.app.impl.DefaultPlayer;

import java.text.MessageFormat;

import com.dol.cdf.common.MessageCode;
import com.dol.cdf.common.config.AllGameConfig;
import com.dol.cdf.common.config.ItemConstant;
import com.dol.cdf.log.LogConst;
import com.dol.cdf.log.LogUtil;

public enum ItemGroupEnum {

	NULL {
		@Override
		public int doBonus(Player player, String type, int amount, int bonusType, boolean isBonus) {
			return MessageCode.OK;
		}

		@Override
		public String getDesc(Player lobby, String type, int amount, boolean isBonus) {
			return "";
		}

		@Override
		public int check(Player player, String type, int amount, boolean isBonus) {
			return MessageCode.OK;
		}
	},
	currency {
		@Override
		public int doBonus(Player player, String type, int amount, int bonusType, boolean isBonus) {
			int code = check(player, type, amount, isBonus);
			if (code == MessageCode.OK) {
				if (!isBonus) {
					amount = -amount;
				}
				player.getProperty().changeMoney(type, amount, bonusType, player);
				if (isBonus) {
					LogUtil.doAcquireLog((DefaultPlayer)player, bonusType, Math.abs(amount), player.getProperty().getMoney(type), type);
				} else {
					LogUtil.doMoneyCostLog((DefaultPlayer)player, bonusType, Math.abs(amount), player.getProperty().getMoney(type), type);
				}
				
			}
			return code;
		}

		@Override
		public String getDesc(Player lobby, String type, int amount, boolean isBonus) {
			if (isBonus) {
				if (type.equals("gold")) {
					return MessageFormat.format(AllGameConfig.getInstance().text.getText(MessageCode.EARN_GOLD), amount) + "\n";
				} else if (type.equals("silver")) {
					return MessageFormat.format(AllGameConfig.getInstance().text.getText(MessageCode.EARN_SILVER), amount) + "\n";
				} else {
					return "";
				}
			} else {
				if (type.equals("gold")) {
					return MessageFormat.format(AllGameConfig.getInstance().text.getText(MessageCode.COST_GOLD), amount) + "\n";
				} else if (type.equals("silver")) {
					return MessageFormat.format(AllGameConfig.getInstance().text.getText(MessageCode.COST_SILVER), amount) + "\n";
				} else {
					return "";
				}
			}
		}

		@Override
		public int check(Player player, String type, int amount, boolean isBonus) {
			if (!isBonus) {
				return player.getProperty().hasEnoughMoney(type, Math.abs(amount)) ? MessageCode.OK : MessageCode.FAIL;
			}
			return MessageCode.OK;
		}
	},

	energy {
		@Override
		public int doBonus(Player player, String type, int amount, int bonusType, boolean isBonus) {
			int code = check(player, type, amount, isBonus);
			if (code == MessageCode.OK) {
				if (!isBonus) {
					amount = -amount;
				}
				// 建筑拉面馆给予
				// if (bonusType == LogConst.BUILDING_GIVE) {
				// player.getProperty().addEnergyLimit(amount);
				// } else {
				player.getProperty().addEnergy(amount);
				// }

			}
			if (isBonus) {
				LogUtil.doGetEnergyLog((DefaultPlayer)player, bonusType, amount);
			}
			return code;
		}

		@Override
		public String getDesc(Player player, String type, int amount, boolean isBonus) {
			return null;
		}

		@Override
		public int check(Player player, String type, int amount, boolean isBonus) {
			if (!isBonus) {
				return player.getProperty().hasEnoughEnergy(Math.abs(amount)) ? MessageCode.OK : MessageCode.ENERGY_NOT_ENUGH;
			}
			return MessageCode.OK;
		}

	},
	spoint {
		@Override
		public int doBonus(Player player, String type, int amount, int bonusType, boolean isBonus) {
			int code = check(player, type, amount, isBonus);
			if (code == MessageCode.OK) {
				if (!isBonus) {
					amount = -amount;
				}
				player.getProperty().addSpoint(amount);
				//通过使用秘籍获取获得专精点埋点
				if(bonusType==LogConst.GIVE_ITEM && isBonus)
				{
					LogUtil.doPointLog(player, 2, 0);
				}
				
			}
			return code;
		}

		@Override
		public String getDesc(Player player, String type, int amount, boolean isBonus) {
			return null;
		}

		@Override
		public int check(Player player, String type, int amount, boolean isBonus) {
			if (!isBonus) {
				return player.getProperty().hasEnoughSpoint(Math.abs(amount)) ? MessageCode.OK : MessageCode.SPOINT_NOT_ENUGH;
			}
			return MessageCode.OK;
		}

	},
	bag {

		@Override
		public int doBonus(Player player, String type, int amount, int bonusType, boolean isBonus) {
			int itemId = Integer.parseInt(type);
			int code = MessageCode.OK;
			if (isBonus) {
				player.getInvenotry().addItem(itemId, amount, bonusType, player);
				LogUtil.doGetItemLog((DefaultPlayer)player, bonusType, itemId, amount);
			}else {
				code = check(player, type, amount, isBonus);
				if (code == MessageCode.OK) {
					player.getInvenotry().rmItemByItemId(itemId, amount, bonusType, player);
					//LogUtil.doRemoveItemLog((DefaultPlayer)player, bonusType, itemId, amount);
				}
			}
			return code;
		}

		@Override
		public int check(Player player, String type, int amount, boolean isBonus) {
			if (!isBonus) {
				amount = -amount;
			}
			int itemId = Integer.parseInt(type);
			boolean isOk = player.getInvenotry().checkItem(itemId, amount);
			if(isOk) {
				return MessageCode.OK;
			}else {
				int containerId = AllGameConfig.getInstance().items.getItemContainerId(itemId);
				if(isBonus) {
					return ItemConstant.BAG_FULL_CODE[containerId];
				}else {
					return ItemConstant.BAG_LACK_CODE[containerId];
				}
			}
			
		}

		@Override
		public String getDesc(Player player, String type, int amount, boolean isBonus) {
			BaseItem baseItem = AllGameConfig.getInstance().items.getBaseItem(Integer.parseInt(type));
			if (isBonus) {
				return MessageFormat.format(AllGameConfig.getInstance().text.getText(MessageCode.EARN_ITEM), baseItem.getAlt(), amount) + "\n";
			} else {
				return MessageFormat.format(AllGameConfig.getInstance().text.getText(MessageCode.COST_ITEM), baseItem.getAlt(), amount) + "\n";
			}
		}
	},
	exp {
		@Override
		public int check(Player player, String type, int amount, boolean isBonus) {
			return MessageCode.OK;
		}

		@Override
		public int doBonus(Player player, String type, int amount, int bonusType, boolean isBonus) {
			player.getProperty().addExp(amount, player);
			LogUtil.doAcquireLog((DefaultPlayer)player, LogConst.EXAM_END_REWARD, amount, player.getProperty().getExp(), "exp");
			return MessageCode.OK;
		}

		@Override
		public String getDesc(Player player, String type, int amount, boolean isBonus) {
			return MessageFormat.format(AllGameConfig.getInstance().text.getText(MessageCode.EARN_EXP), amount) + "\n";
		}
	},

	hero {
		@Override
		public int check(Player player, String type, int amount, boolean isBonus) {

			boolean isMaxHeros = player.getHeros().isMaxHeros(1);
			if (!isMaxHeros) {
				return MessageCode.OK;
			} else {
				return MessageCode.HERO_FULL;
			}
		}

		@Override
		public int doBonus(Player player, String type, int amount, int bonusType, boolean isBonus) {
			if (check(player, type, amount, isBonus) == MessageCode.OK) {
				int heroId = player.getHeros().addHero(Integer.parseInt(type));
				//忍者获取埋点
				LogUtil.doNinjaLog(player, bonusType, player.getHeros().getAllHero().get(heroId).getRoleId());
				return MessageCode.OK;
			} else {
				player.getMail().addSysItemMail(Integer.parseInt(type), 1, bonusType);
				return MessageCode.HERO_TO_MAIL;
			}

		}

		@Override
		public String getDesc(Player player, String type, int amount, boolean isBonus) {
			return MessageFormat.format(AllGameConfig.getInstance().text.getText(MessageCode.EARN_EXP), amount) + "\n";
		}
	},

	rxp {
		@Override
		public int check(Player player, String type, int amount, boolean isBonus) {
			return MessageCode.OK;
		}

		@Override
		public int doBonus(Player player, String type, int amount, int bonusType, boolean isBonus) {
			player.getHeros().getHero(Integer.parseInt(type)).addExp(amount, player);
			return MessageCode.OK;

		}

		@Override
		public String getDesc(Player player, String type, int amount, boolean isBonus) {
			return MessageFormat.format(AllGameConfig.getInstance().text.getText(MessageCode.EARN_EXP), amount) + "\n";
		}
	},

	;

	/**
	 * @param amount
	 *            整数为奖励负数为消耗 检测是否是否有奖励，有返回ok，没有返回false id，
	 * @param isBonus
	 *            TODO
	 * 
	 */
	public abstract int doBonus(Player player, String type, int amount, int bonusType, boolean isBonus);

	/**
	 * @param amount
	 *            整数为奖励负数为消耗 获得奖励的内容描述
	 * @param isBonus
	 *            TODO
	 */
	public abstract String getDesc(Player player, String type, int amount, boolean isBonus);

	/**
	 * 检测是否可以给奖励
	 * 
	 * @param type
	 * @param amount
	 * @param isBonus
	 *            TODO
	 * @param lobby
	 * @param isBonus
	 *            true-奖励 false-扣除
	 * 
	 * @return
	 */
	public abstract int check(Player player, String type, int amount, boolean isBonus);

}
