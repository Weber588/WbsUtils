package wbs.utils;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.util.Ticks;
import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.Vault;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.generator.structure.GeneratedStructure;
import org.bukkit.generator.structure.Structure;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import wbs.utils.util.WbsColours;
import wbs.utils.util.WbsKeyed;
import wbs.utils.util.WbsMath;
import wbs.utils.util.commands.brigadier.WbsCommand;
import wbs.utils.util.commands.brigadier.WbsSubcommand;
import wbs.utils.util.commands.brigadier.argument.WbsSimpleArgument;
import wbs.utils.util.entities.state.EntityStateManager;
import wbs.utils.util.particles.CuboidParticleEffect;
import wbs.utils.util.particles.WbsParticleEffect;
import wbs.utils.util.particles.entity.DisplayParticle;
import wbs.utils.util.particles.entity.TextDisplayParticleBuilder;
import wbs.utils.util.persistent.BlockChunkStorageUtil;
import wbs.utils.util.plugin.WbsPlugin;
import wbs.utils.util.pluginhooks.PluginHookManager;
import wbs.utils.util.pluginhooks.VaultWrapper;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * The base plugin, a simple implementation of {@link WbsPlugin}.
 */
@SuppressWarnings("UnstableApiUsage")
public class WbsUtils extends WbsPlugin {
	
	private static WbsUtils instance = null;
	public static WbsUtils getInstance() {
		return instance;
	}

	private boolean isLoaded = false;

	@Override
	public void onLoad() {
		EntityStateManager.registerNativeDeserializers();

		isLoaded = true;
	}

	@Override
	public void onEnable() {
		instance = this;

		EntityStateManager.registerConfigurableClasses();

		WbsParticleEffect.setPlugin(this);

		WbsCommand.getStatic(this, "utils")
				.addSubcommands(
						WbsSubcommand.simpleSubcommand(this, "reload", context -> {
							configure();
							sendMessage("Reloaded! See console for details.", context.getSource().getSender());
						}),
						WbsSubcommand.simpleSubcommand(this, "trial_key", context -> {
							CommandSender sender = context.getSource().getSender();
                            if (!(sender instanceof Player player)) {
								sendMessage("This command is only usable by players.", sender);
								return;
                            }

							;
							Block targetBlock = player.getTargetBlockExact(5);
                            if (targetBlock == null || !(targetBlock.getState() instanceof Vault vault)) {
								sendMessage("Look at a Vault to get its key.", sender);
								return;
							}

							player.give(vault.getKeyItem());

                        }),
						getLocateSubcommand(),
						WbsSubcommand.simpleSubcommand(this, "testparticle", context -> {
							CommandSender sender = context.getSource().getSender();
							if (!(sender instanceof Player player)) {
								return;
							}

							Vector facingVector = WbsMath.getFacingVector(player);
							Location spawnLocation = player.getEyeLocation().add(facingVector);

							TextDisplayParticleBuilder builder = new TextDisplayParticleBuilder();

							int maxAge = 40;
							builder.setMaxAge(maxAge);
							builder.setTeleportDuration(1);
							builder.setInterpolationDuration(1);

							builder.usePackets(true);

							Color color1 = WbsColours.fromHSB(0.067f, 1, 0.5);
							builder.setColorKeyframe(0f, color1);
							Color color2 = WbsColours.fromHSB(0.067f, 0.9, 0.25);
							builder.setColorKeyframe(0.5f, color2);
							Color color3 = WbsColours.fromHSB(0.067f, 0.001, 0.2);
							builder.setColorKeyframe(maxAge - 1, color3);

							Vector fieldForce = new Vector(0, 0.03, 0);
							builder.setTickForce(fieldForce);
							builder.setDrag(0.25);

							builder.setAngularVelocity(new Vector(0, 0, Math.toRadians(15)));
							builder.setAngularDrag(0.01);

							builder.doBlockCollisions(true);

							double initialSpeed = 0.15;

							runTimerNTimes(runnable -> {
								for (int i = 0; i < 5; i++) {
									Vector initialVelocityDir = WbsMath.scaleVector(WbsMath.randomVector(5).setY(1), Math.max(0.01, Math.random() * initialSpeed));

									builder.configure(particle -> {
										particle.setVelocity(initialVelocityDir);
									});

									builder.setKeyframe(0.25,particle -> {
										particle.setTickForce(fieldForce.clone().add(initialVelocityDir.multiply(-0.035)));
									});

									builder.setKeyframe(0.67,particle -> {
										particle.setTickForce(fieldForce.clone().add(initialVelocityDir.multiply(-1)));
									});

									builder.playParticle(spawnLocation.clone().add(initialVelocityDir));
								}
							}, 250, 1, 1);
						}),
						WbsSubcommand.simpleSubcommand(this, "whereami", context -> {
							CommandSender sender = context.getSource().getSender();

                            if (!(sender instanceof Player player)) {
                              	sendMessage("This command is only usable by players.", sender);
                                return;
                            }

							Location location = player.getLocation();

							World world = location.getWorld();

							sendMessage("You are currently at "
											+ WbsMath.roundTo(location.getX(), 2) + ", "
											+ WbsMath.roundTo(location.getY(), 2) + ", "
											+ WbsMath.roundTo(location.getZ(), 2) + " in the world \""
											+ world.getName() + "\"",
									sender);

							Biome biome = world.getBiome(location);
							buildMessage("The biome is ")
									.append(Component.translatable(biome.translationKey(), WbsKeyed.toPrettyString(biome)))
									.append(", with humidity of " + WbsMath.roundTo(world.getHumidity(location.getBlockX(), location.getBlockY(), location.getBlockZ()), 2))
									.append(", and a temperature of " + WbsMath.roundTo(world.getTemperature(location.getBlockX(), location.getBlockY(), location.getBlockZ()), 2))
									.send(sender);

                            Set<GeneratedStructure> structures = location.getChunk().getStructures().stream()
                                    .filter(structure ->
                                            structure.getPieces().stream().anyMatch(
                                                    piece -> piece.getBoundingBox().contains(location.toVector())
                                            )
                                    ).collect(Collectors.toSet());

                            if (structures.isEmpty()) {
                                sendMessage("You are not currently in any structures.", sender);
                            } else {
                                sendMessage("You are standing in the following structure(s):", sender);
                                structures.forEach(generatedStructure -> {
									Structure structure = generatedStructure.getStructure();
									Key structureKey = RegistryAccess.registryAccess().getRegistry(RegistryKey.STRUCTURE).getKey(structure);

									int hash;
                                    if (structureKey != null) {
										hash = structureKey.hashCode();
										sendMessageNoPrefix(" &h- " + structureKey.asString(), sender);
									} else {
										sendMessageNoPrefix(" &h- Unknown (" + structure.getStructureType().key().asString() + ")", sender);
										hash = structure.hashCode();
									}

									CuboidParticleEffect outlineEffect = new CuboidParticleEffect();
									outlineEffect.setScaleAmount(true)
											.setAmount(2);

									outlineEffect.setOptions(new Particle.DustOptions(WbsColours.fromHSB(new Random(hash).nextDouble(), 1, 1), 2f));

									runTimerNTimes(task -> {
										generatedStructure.getPieces().forEach(piece -> {
											BoundingBox pieceBox = piece.getBoundingBox();

											Location playLocation = outlineEffect.configureBlockOutline(
													pieceBox.getMax().toLocation(world),
													pieceBox.getMin().toLocation(world)
											);

											outlineEffect.build();
											outlineEffect.play(Particle.DUST, playLocation, player);
										});
									}, 5, 0, 20);
                                });
                            }

							PersistentDataContainer blockContainer = BlockChunkStorageUtil.getContainer(location.getBlock());
							if (!blockContainer.getKeys().isEmpty()) {
								sendMessage("The block container at your location contains the following keys:", sender);
								for (NamespacedKey key : blockContainer.getKeys()) {
									sendMessageNoPrefix(" &h- " + key.asString(), sender);
								}
							}
						})
				).addAliases("wbsutils", "wbsutils:utils", "wbsutils:wbsutils")
				.setPermission("wbsutils.command")
				.register();

		// TODO: Actually add a config omg
		setDisplays("&8[&7WbsUtils&8]", ChatColor.GRAY, ChatColor.AQUA, ChatColor.RED);

		configure();
	}

	private static @NotNull String colourString(Color color1) {
		return color1.getRed() + ", " + color1.getGreen() + ", " + color1.getBlue();
	}

	private static @NotNull String colourStringHSV(Color color) {
		double[] hsv = WbsColours.getHSV(color);
		return hsv[0] + ", " + hsv[1] + ", " + hsv[2];
	}

	private WbsSubcommand getLocateSubcommand() {
		WbsSimpleArgument.KeyedSimpleArgument arg = new WbsSimpleArgument.KeyedSimpleArgument("label", ArgumentTypes.namespacedKey(), null);
		return new WbsSubcommand(this, "locate", "wbsutils.locate") {
			@Override
			protected int executeNoArgs(CommandContext<CommandSourceStack> context) {
				return 0;
			}

			@Override
			protected int onSimpleArgumentCallback(CommandContext<CommandSourceStack> context, WbsSimpleArgument.ConfiguredArgumentMap configuredArgumentMap) {
				NamespacedKey key = configuredArgumentMap.get(arg);
				CommandSender sender = context.getSource().getSender();
				if (key != null) {
					Structure structure = RegistryAccess.registryAccess().getRegistry(RegistryKey.STRUCTURE).get(key);
					if (structure != null) {
						getAsync(() -> {
							if (sender instanceof Player player) {
								return player.getWorld().locateNearestStructure(player.getLocation(), structure, 20000, true);
							}
							return null;
						}, result -> {
							if (result == null) {
								sendMessage("Structure not found.", sender);
							} else {
								Location location = result.getLocation();
								sendMessage("Structure found at " + location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ(), sender);
							}
						});
					} else {
						sendMessage("Invalid structure: " + key, sender);
					}
				} else {
					sendMessage("Specify a structure.", sender);
				}

				return Command.SINGLE_SUCCESS;
			}
		}.addSimpleArgument(arg);
	}

	public void configure() {
		PluginHookManager.isConfigured = false;
		PluginHookManager.configure();

		VaultWrapper.isConfigured = false;
		VaultWrapper.configure();
	}

    @Override
    public void onDisable() {
    	
    }

	public boolean isLoaded() {
		return isLoaded;
	}
}
