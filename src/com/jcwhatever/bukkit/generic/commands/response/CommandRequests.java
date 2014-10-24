package com.jcwhatever.bukkit.generic.commands.response;

import com.jcwhatever.bukkit.generic.collections.SetMap;
import com.jcwhatever.bukkit.generic.messaging.Messenger;
import com.jcwhatever.bukkit.generic.utils.TextUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public class CommandRequests {

    private CommandRequests() {}

    private static SetMap<CommandSender, ResponseRequest> _requests = new SetMap<>(25);

    /**
     * Get a list of response requests for the specified command sender.
     *
     * @param sender  The command sender.
     */
    @Nullable
    public static List<ResponseRequest> getRequests(CommandSender sender) {

        Set<ResponseRequest> requests = _requests.getAll(sender);
        if (requests == null)
            return null;

        return new ArrayList<>(requests);
    }

    /**
     * Request a response from a player.
     *
     * @param request  {@code ResponseRequest} containing info about the request.
     */
    public static void request(ResponseRequest request) {
        _requests.put(request.getCommandSender(), request);
    }

    /**
     * Request a response from a player.
     *
     * @param plugin        The owning plugin.
     * @param context       The context name of the request.
     * @param sender        The command sender to make the request to.
     * @param handler       The response handler.
     * @param responseType  The type of responses expected.
     *
     * @return  {@code ResponseRequest} object.
     */
    public static ResponseRequest request(Plugin plugin, String context, CommandSender sender, ResponseHandler handler, ResponseType... responseType) {
        ResponseRequest request = new ResponseRequest(plugin, context, sender, handler, responseType);
        _requests.put(request.getCommandSender(), request);

        return request;
    }

    /**
     * Cancel a request for a response from a player.
     * @param request
     */
    public static void cancel(ResponseRequest request) {
        _requests.removeValue(request.getCommandSender(), request);
    }

    /**
     * Called to check for and process a response.
     *
     * @param sender   The command sender.
     * @param message  The command message.
     *
     * @return  True if the message was a request response.
     */
    public static boolean onResponse(CommandSender sender, String message) {

        String[] messageComp = TextUtils.PATTERN_SPACE.split(message);

        if (messageComp.length > 2 || messageComp[0].length() == 0)
            return false; // finished

        String commandName = messageComp[0].substring(1);
        String context = messageComp.length > 1 ? messageComp[1] : null;

        if (commandName.isEmpty())
            return false;

        ResponseType type = ResponseType.from(commandName);
        if (type == null)
            return false; // finished

        List<ResponseRequest> allRequests = CommandRequests.getRequests(sender);
        if (allRequests == null || allRequests.isEmpty())
            return true; // finished

        List<ResponseRequest> requests = new ArrayList<>(allRequests.size());

        for (ResponseRequest request : allRequests) {
            if (request.getResponseTypes().contains(type))
                requests.add(request);
        }

        if (requests.isEmpty()) {
            return false; // finished
        }
        else if (requests.size() == 1) {
            ResponseRequest request = requests.get(0);

            // make sure the response is expected
            if (!request.getResponseTypes().contains(type))
                return false; // finished

            handleResponse(sender, request, type);
        }
        else if (context == null) {
            tellMultipleRequests(sender, type, requests);
        }
        else {

            for (ResponseRequest request : requests) {
                if (request.getContext().equalsIgnoreCase(context) &&
                        request.getResponseTypes().contains(type)) {

                    handleResponse(sender, request, type);

                    return true; // finished
                }
            }
            tellMultipleRequests(sender, type, requests);
        }
        return true;
    }

    private static void handleResponse(CommandSender sender, ResponseRequest responseRequest, ResponseType type) {
        responseRequest.getHandler().onResponse(type);
        _requests.removeValue(sender, responseRequest);
    }

    private static void tellMultipleRequests(CommandSender sender, ResponseType type, Collection<ResponseRequest> requests) {
        Messenger.tell(sender, "{YELLOW}Multiple requests for response found. Please be more specific:");

        for (ResponseRequest request : requests) {
            Messenger.tell(sender, '/' + type.getCommandName() + ' ' + request.getContext());
        }
    }

}
