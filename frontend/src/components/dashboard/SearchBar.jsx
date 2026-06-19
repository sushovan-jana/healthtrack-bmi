import React, { useState, useEffect } from 'react';
import './SearchBar.css';

const SearchBar = ({
  placeholder = 'Search by name or phone number...',
  onSearch,
  initialValue = '',
}) => {
  const [query, setQuery] = useState(initialValue);

  useEffect(() => {
    const handler = setTimeout(() => {
      onSearch(query);
    }, 400); // 400ms debounce delay

    return () => {
      clearTimeout(handler);
    };
  }, [query, onSearch]);

  const handleClear = () => {
    setQuery('');
  };

  return (
    <div className="search-bar-wrapper">
      <span className="search-icon">🔍</span>
      <input
        type="text"
        className="search-input"
        placeholder={placeholder}
        value={query}
        onChange={(e) => setQuery(e.target.value)}
      />
      {query && (
        <button onClick={handleClear} className="search-clear-btn" type="button">
          ✕
        </button>
      )}
    </div>
  );
};

export default SearchBar;
