package org.opennms.features.poller.remote.gwt.client.remoteevents;

import org.opennms.features.poller.remote.gwt.client.RemotePollerPresenter;

import com.google.gwt.user.client.Window;

public class GeocodingFinishedRemoteEvent implements MapRemoteEvent {
	private static final long serialVersionUID = 1L;
	private int m_size = 0;

	public GeocodingFinishedRemoteEvent() {}

	public GeocodingFinishedRemoteEvent(final int size) {
		m_size = size;
	}

	public void dispatch(final RemotePollerPresenter presenter) {
//		Window.alert("Updated geocoding on " + m_size + " location definitions.");
	}
}
