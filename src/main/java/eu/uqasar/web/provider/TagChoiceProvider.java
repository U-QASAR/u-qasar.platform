/**
 * 
 */
package eu.uqasar.web.provider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


import com.vaynberg.wicket.select2.Response;
import com.vaynberg.wicket.select2.TextChoiceProvider;
import java.util.Arrays;

/**
 *
 *
 */
public class TagChoiceProvider extends TextChoiceProvider<String> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6769781095146146501L;
	
	public static final List<String> tags = Arrays.asList("Albania", "Bahamas",
			"Cambodia", "Denmark", "Ecuador", "Fiji", "Gabon", "Haiti",
			"Iceland", "Jamaica", "Kazakhstan", "Laos", "Macedonia", "Namibia",
			"Oman", "Pakistan", "Qatar", "Romania", "Saudi Arabia", "Taiwan",
			"Uganda", "Vietnam", "Wales", "Yemen", "Zimbabwe");
	
	@Override
	protected String getDisplayText(String choice) {
		return choice;
	}

	@Override
	protected Object getId(String choice) {
		return choice;
	}

	@Override
	public void query(String term, int page, Response<String> response) {
		response.addAll(queryMatches(term, page, 10));
		response.setHasMore(response.size() == 10);
	}

	@Override
	public Collection<String> toChoices(Collection<String> ids) {
		List<String> items = new ArrayList<>();
		for (String id : ids) {
			items.add(id.trim());
		}
		return items;
	}
	
	private List<String> queryMatches(String term, final int page,
			final int pageSize) {
		List<String> result = new ArrayList<>();
		final int offset = page * pageSize;
		int matched = 0;
		for (String tag : tags) {
			if (result.size() == pageSize) {
				break;
			}
			if (tag.toUpperCase().contains(term.toUpperCase())) {
				matched++;
				if (matched > offset) {
					result.add(tag);
				}
			}
		}
		return result;
	}
}