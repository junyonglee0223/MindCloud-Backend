document.addEventListener('DOMContentLoaded', function() {
  const saveBookmarkBtn = document.getElementById('saveBookmarkBtn');
  const tagContainer = document.getElementById('tagContainer');
  const bookmarkList = document.getElementById('bookmarkList');
  const goToSearchBtn = document.getElementById('goToSearchBtn');

  // 검색 버튼 클릭 시 search.html로 이동
  goToSearchBtn.addEventListener('click', function () {
    window.location.href = 'search.html';
  });

  // "북마크 저장하기" 버튼 클릭 이벤트
  saveBookmarkBtn.addEventListener('click', function () {
    chrome.tabs.query({ active: true, currentWindow: true }, function (tabs) {
      const currentTab = tabs[0];

      // GPT API 호출하여 태그 생성
      fetchGPTTags(currentTab.url, currentTab.title)
        .then((tags) => {
          // 북마크 데이터 생성 및 팝업 열기
          const bookmark = {
            title: currentTab.title,
            url: currentTab.url,
            tags: tags, // GPT로 생성된 태그 추가
          };
          openEditPopup(bookmark); // edit-popup.js의 함수 호출
        })
        .catch((error) => {
          console.error('태그 생성 중 오류 발생:', error);
          alert('태그를 생성하는 데 문제가 발생했습니다. 다시 시도해주세요.');
        });
      });
  });


  function fetchGPTTags(url, title) {
    const apiKey = config.OPENAI_API_KEY;
    const apiUrl = 'https://api.openai.com/v1/chat/completions';

    const prompt = `Generate 6 tags in korean for the following website based on its URL and title: URL: ${url}, Title: ${title}. Output format: tag1, tag2, tag3, tag4, tag5, tag6`;

    return fetch(apiUrl, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${apiKey}`
      },
      body: JSON.stringify({
        model: 'gpt-3.5-turbo',
        messages: [
          { role: 'system', content: 'You are an assistant that generates tags for websites.' },
          { role: 'user', content: prompt }
        ],
        max_tokens: 50
      })
    })
    .then(response => {
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      return response.json();
    })
    .then(data => {
      if (data.choices && data.choices.length > 0 && data.choices[0].message.content) {
        const tags = data.choices[0].message.content.trim().split(',').map(tag => tag.trim()).slice(0, 6); // 쉼표로 분리하여 태그 배열 생성
        return tags;
      } else {
        throw new Error('GPT 응답에서 태그를 생성할 수 없습니다.');
      }
    })
    .catch(error => {
      console.error('GPT API 호출 중 오류 발생:', error);
      return [];
    });
  }

  // 모든 태그 표시
  function displayTags() {
    chrome.storage.sync.get({ bookmarks: [] }, function(data) {
      const bookmarks = data.bookmarks;
      tagContainer.innerHTML = ''; // 기존 태그 목록 초기화
      const colors = ['#FF6666', '#FFB266', '#FFFF66', '#B2FF66', '#66FF66', '#66FFB2']; // 버튼 색상 배열
      let colorIndex = 0; // 색상 인덱스 초기화
      
      const uniqueTags = new Set(); //중복 태그 방지

      bookmarks.forEach(bookmark => {
        bookmark.tags.forEach(tag => {
          if(!uniqueTags.has(tag)){
            uniqueTags.add(tag);
            
            const tagElement = document.createElement('button'); // 버튼으로 변경
            tagElement.className = 'tag';
            tagElement.textContent = tag; // 태그 표시
            tagElement.style.backgroundColor = colors[colorIndex]; // 버튼 색상 지정
            tagElement.style.margin = '5px'; // 버튼 간격 지정
            tagElement.addEventListener('click', function() {
              filterBookmarksByTag(tag); // 태그 클릭 시 해당 북마크 필터링
            });
            tagContainer.appendChild(tagElement);
            colorIndex = (colorIndex + 1) % colors.length; // 색상 인덱스 순환
          }
        });
      });
    });
  }

  // 태그로 북마크 필터링
  function filterBookmarksByTag(tag) {
    chrome.storage.sync.get({ bookmarks: [] }, function(data) {
      const bookmarks = data.bookmarks.filter(bookmark => bookmark.tags.includes(tag));
      displayBookmarks(bookmarks);
    });
  }

  // 북마크 목록 표시
  function displayBookmarks(bookmarks) {
    bookmarkList.innerHTML = ''; // 기존 목록 초기화
    bookmarks.forEach(function(bookmark) {
      const listItem = document.createElement('li');
      listItem.innerHTML = `<a href="${bookmark.url}" target="_blank">${bookmark.title}</a><p>Tags: ${bookmark.tags.join(', ')}</p>`;
      bookmarkList.appendChild(listItem);
    });
  }

  // // 초기화 시 태그 목록과 북마크 목록 표시
  // displayTags();
  // chrome.storage.sync.get({ bookmarks: [] }, function(data) {
  //   displayBookmarks(data.bookmarks);
  // });

  
    // 초기화 함수
  function initializeData() {
    const userEmail = "test1@gmail.com";
    ///clear start-----------------
    chrome.storage.sync.clear(function(){             
        // 1. 백엔드에서 모든 북마크 가져오기
        getAllBookmarksFromBackend(userEmail)
          .then((bookmarks) => {
            // 2. chrome.storage.sync에 북마크 저장
            chrome.storage.sync.set({ bookmarks: bookmarks }, function () {
              console.log("북마크가 chrome.storage.sync에 저장되었습니다:", bookmarks);
    
              // 3. 태그와 북마크 목록 표시
              displayTags();
              displayBookmarks(bookmarks);
            });
          })
          .catch((error) => {
            console.error("초기화 중 오류 발생:", error);
          });
    });
    ///clear end-----------------------
  }
  window.initializeData = initializeData;
  initializeData();
});
