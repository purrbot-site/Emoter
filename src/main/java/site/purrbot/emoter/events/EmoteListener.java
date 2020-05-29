package site.purrbot.emoter.events;

import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.emote.EmoteAddedEvent;
import net.dv8tion.jda.api.events.emote.EmoteRemovedEvent;
import net.dv8tion.jda.api.events.emote.update.EmoteUpdateNameEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import site.purrbot.emoter.Emoter;

import javax.annotation.Nonnull;

public class EmoteListener extends ListenerAdapter{
    
    private final Emoter bot;
    
    public EmoteListener(Emoter bot){
        this.bot = bot;
    }
    
    @Override
    public void onEmoteAdded(@Nonnull EmoteAddedEvent event){
        Guild guild = event.getGuild();
        Emote emote = event.getEmote();
    
        bot.updateMessage(emote.isAnimated() ? Emoter.EmoteType.ANIMATED : Emoter.EmoteType.NORMAL, guild);
    
        TextChannel tc = guild.getTextChannelById(bot.getFileManager().getString("config", "log"));
        if(tc == null)
            return;
        
        bot.sendLog(tc, Emoter.LogType.ADDED, emote);
    }
    
    @Override
    public void onEmoteUpdateName(@Nonnull EmoteUpdateNameEvent event){
        Guild guild = event.getGuild();
        Emote emote = event.getEmote();
    
        bot.updateMessage(emote.isAnimated() ? Emoter.EmoteType.ANIMATED : Emoter.EmoteType.NORMAL, guild);
    
        TextChannel tc = guild.getTextChannelById(bot.getFileManager().getString("config", "log"));
        if(tc == null)
            return;
    
        bot.sendLog(tc, Emoter.LogType.EDITED, emote);
    }
    
    @Override
    public void onEmoteRemoved(@Nonnull EmoteRemovedEvent event){
        Guild guild = event.getGuild();
        Emote emote = event.getEmote();
    
        bot.updateMessage(emote.isAnimated() ? Emoter.EmoteType.ANIMATED : Emoter.EmoteType.NORMAL, guild);
    
        TextChannel tc = guild.getTextChannelById(bot.getFileManager().getString("config", "log"));
        if(tc == null)
            return;
    
        bot.sendLog(tc, Emoter.LogType.REMOVED, emote);
    }
}
