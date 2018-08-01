package cn.yang.server.commandhandler;

import cn.yang.common.dto.Request;
import cn.yang.common.command.Commands;
import cn.yang.common.constant.ExceptionConstants;
import cn.yang.server.netty.ChannelPair;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

import static cn.yang.common.constant.ExceptionConstants.PUPPET_CONNECTION_LOST;

/**
 * @author Cool-Coding
 *         2018/7/27
 */
public class TerminateCommandHandler extends AbstractServerCommandHandler{
    @Override
    public void handle0(ChannelHandlerContext ctx, Request request) throws Exception {
        final String puppetName = request.getPuppetName();
        ChannelPair channelPair = CONNECTED_CHANNELPAIRS.get(puppetName);
        if(channelPair!=null) {
            channelPair.setMasterChannel(null);
        }
        //检查傀儡端连接是否正常
        if (channelPair==null || channelPair.getPuppetChannel()==null || !channelPair.getPuppetChannel().isOpen()){
            error(request,ExceptionConstants.PUPPET_CONNECTION_LOST);
            sendError(request, ctx,PUPPET_CONNECTION_LOST);
            return;
        }

        //发送数据
        final Channel puppetChannel = channelPair.getPuppetChannel();
        puppetChannel.writeAndFlush(buildResponse(request, Commands.TERMINATE));
    }
}