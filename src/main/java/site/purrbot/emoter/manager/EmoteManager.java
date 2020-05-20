package site.purrbot.emoter.manager;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class EmoteManager{
    
    
    public MessageEmbed getEmbed(Guild guild, DBManager.EmoteType type){
        List<Emote> emotes;
        if(type.equals(DBManager.EmoteType.ANIMATED))
            emotes = guild.getEmotes().stream()
                    .filter(Emote::isAnimated)
                    .sorted(Comparator.comparing(Emote::getName))
                    .collect(Collectors.toList());
        else
            emotes = guild.getEmotes().stream()
                    .filter(emote -> !emote.isAnimated())
                    .sorted(Comparator.comparing(Emote::getName))
                    .collect(Collectors.toList());
        
        StringBuilder sb = new StringBuilder();
        EmbedBuilder embed = new EmbedBuilder()
                .setColor(0x802F3136)
                .setTitle(firstUpperCase(type.name()));
        for(Emote emote : emotes){
            if(sb.length() + getEmoteInfo(emote).length() + 10 > MessageEmbed.VALUE_MAX_LENGTH){
                embed.addField(
                        EmbedBuilder.ZERO_WIDTH_SPACE,
                        sb.toString(),
                        false
                );
                sb = new StringBuilder(getEmoteInfo(emote)).append("\n");
            }
            
            sb.append(getEmoteInfo(emote)).append("\n");
        }
        
        if(sb.length() > 0)
            embed.addField(
                    EmbedBuilder.ZERO_WIDTH_SPACE,
                    sb.toString(),
                    false
            );
        
        return embed.build();
    }
    
    private String getEmoteInfo(Emote emote){
        return String.format(
                "%s `%-25s - %s`",
                emote.getAsMention(),
                ":" + emote.getName() + ":",
                emote.getAsMention()
        );
    }
    
    private String firstUpperCase(String text){
        return Character.toString(text.charAt(0)).toUpperCase() + text.substring(1).toLowerCase();
    }
}
