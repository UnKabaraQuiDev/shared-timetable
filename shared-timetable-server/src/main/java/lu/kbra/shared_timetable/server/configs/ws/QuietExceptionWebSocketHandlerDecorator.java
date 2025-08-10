package lu.kbra.shared_timetable.server.configs.ws;

import java.util.logging.Logger;

import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.WebSocketHandlerDecorator;

import lu.kbra.shared_timetable.server.STMain;
import lu.pcy113.pclib.PCUtils;

public class QuietExceptionWebSocketHandlerDecorator extends WebSocketHandlerDecorator {

	private static final Logger LOGGER = Logger.getLogger(QuietExceptionWebSocketHandlerDecorator.class.getName());

	public QuietExceptionWebSocketHandlerDecorator(WebSocketHandler delegate) {
		super(delegate);
	}

	@Override
	public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) {
		try {
			super.handleMessage(session, message);
		} catch (Exception ex) {
			if (ex instanceof ResponseStatusException) {
				LOGGER.warning("Handled error: " + ex.getMessage() + " (" + ex.getClass().getSimpleName() + ")");
				if (STMain.DEBUG) {
					PCUtils.getCause(ex).printStackTrace();
				}
			} else {
				LOGGER.warning("WebSocket message handling failed: " + ex.getMessage() + " ("
						+ ex.getClass().getSimpleName() + ") -> " + PCUtils.getRootCauseMessage(ex));
				if (STMain.DEBUG) {
					PCUtils.getCause(ex).printStackTrace();
				}
			}

			try {
				session.close(CloseStatus.SERVER_ERROR);
			} catch (Exception e) {
				LOGGER.severe("Couldn't close websocket session: " + e.getMessage() + " ("
						+ ex.getClass().getSimpleName() + ")");
				if (STMain.DEBUG) {
					PCUtils.getCause(e).printStackTrace();
				}
			}
		}
	}

}