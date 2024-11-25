document.addEventListener('DOMContentLoaded', function () {
    const searchInput = document.getElementById('searchInput');
    const searchBtn = document.getElementById('searchBtn');
    const goBackBtn = document.getElementById('goBackBtn');
    const searchResults = document.getElementById('searchResults');
  
    // 검색 버튼 클릭 이벤트
    searchBtn.addEventListener('click', function () {
      const query = searchInput.value.trim();
      if (!query) {
        alert('검색어를 입력하세요.');
        return;
      }
  
      // chrome.storage.sync에서 검색어 기반 북마크 및 태그 필터링
      chrome.storage.sync.get({ bookmarks: [] }, function (data) {
        const bookmarks = data.bookmarks;
  
        // 태그와 북마크 이름으로 각각 필터링
        const tagResults = bookmarks.filter(bookmark =>
          bookmark.tags.some(tag => tag.includes(query))
        );
        const bookmarkResults = bookmarks.filter(bookmark =>
          bookmark.title.includes(query)
        );
  
        // 결과 출력
        displaySearchResults(tagResults, bookmarkResults, query);
      });
    });
  
    // 뒤로 가기 버튼 클릭 이벤트
    goBackBtn.addEventListener('click', function () {
      window.location.href = 'popup.html'; // popup.html로 이동
    });
  
    // 검색 결과 표시 함수
    function displaySearchResults(tagResults, bookmarkResults, query) {
      searchResults.innerHTML = ''; // 기존 결과 초기화
  
      if (tagResults.length === 0 && bookmarkResults.length === 0) {
        searchResults.innerHTML = `<li>검색어 "${query}"에 대한 결과가 없습니다.</li>`;
        return;
      }
  
      // 태그 검색 결과 출력
      if (tagResults.length > 0) {
        const tagHeader = document.createElement('h3');
        tagHeader.textContent = '태그로 검색된 결과';
        searchResults.appendChild(tagHeader);
  
        tagResults.forEach(function (bookmark) {
          const listItem = document.createElement('li');
          listItem.innerHTML = `<a href="${bookmark.url}" target="_blank">${bookmark.title}</a><p>Tags: ${bookmark.tags.join(', ')}</p>`;
          searchResults.appendChild(listItem);
        });
      }
  
      // 북마크 이름 검색 결과 출력
      if (bookmarkResults.length > 0) {
        const bookmarkHeader = document.createElement('h3');
        bookmarkHeader.textContent = '북마크 이름으로 검색된 결과';
        searchResults.appendChild(bookmarkHeader);
  
        bookmarkResults.forEach(function (bookmark) {
          const listItem = document.createElement('li');
          listItem.innerHTML = `<a href="${bookmark.url}" target="_blank">${bookmark.title}</a><p>Tags: ${bookmark.tags.join(', ')}</p>`;
          searchResults.appendChild(listItem);
        });
      }
    }
  });
  