package org.dinky.service.openai;

import org.dinky.data.dto.SuggestionDTO;

/**
 * OpenAI suggestion service
  Manufacturer
 */
public interface OpenAISuggestionService {

    /**
     * get suggestions for editor
     *
     * @param suggestionDTO suggestionDTO
     * @return suggestions list
     */
     String getSuggestions(SuggestionDTO suggestionDTO) ;
}
