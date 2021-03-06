package org.astropeci.omw.commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.astropeci.omw.commandbuilder.CommandBuilder;
import org.astropeci.omw.commandbuilder.CommandContext;
import org.astropeci.omw.commandbuilder.ExecuteCommand;
import org.astropeci.omw.commandbuilder.ReflectionCommandCallback;
import org.astropeci.omw.commandbuilder.arguments.StringArgument;
import org.astropeci.omw.worlds.ArenaAlreadyExistsException;
import org.astropeci.omw.worlds.ArenaPool;
import org.bukkit.command.TabExecutor;
import org.bukkit.plugin.Plugin;

public class CreateArenaCommand {

    private final ArenaPool arenaPool;
    private final TabExecutor executor;

    public CreateArenaCommand(ArenaPool arenaPool) {
        this.arenaPool = arenaPool;

        executor = new CommandBuilder()
                .addArgument(new StringArgument())
                .build(new ReflectionCommandCallback(this));
    }

    public void register(Plugin plugin) {
        CommandBuilder.registerCommand(
                plugin,
                "arena-create",
                "Creates a new arena",
                "arena-create <name>",
                "omw.arena.create",
                executor
        );
    }

    @ExecuteCommand
    public boolean execute(CommandContext ctx, String name) {
        try {
            arenaPool.create(name);

            TextComponent clickToJoinComponent = new TextComponent("here");
            clickToJoinComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/arena " + name));
            clickToJoinComponent.setBold(true);

            TextComponent message = new TextComponent(
                    new TextComponent("Created " + name + ", click "),
                    clickToJoinComponent,
                    new TextComponent(" to join")
            );
            message.setColor(ChatColor.GREEN);

            ctx.sender.spigot().sendMessage(message);
        } catch (ArenaAlreadyExistsException e) {
            TextComponent message = new TextComponent("An arena with that name already exists");
            message.setColor(ChatColor.RED);
            ctx.sender.spigot().sendMessage(message);
        }

        return true;
    }
}
