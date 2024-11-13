document.addEventListener('DOMContentLoaded', function() {
  const saveBookmarkBtn = document.getElementById('saveBookmarkBtn');
  const tagContainer = document.getElementById('tagContainer');
  const bookmarkList = document.getElementById('bookmarkList');

  // 북마크 저장 버튼 클릭 시 GPT API 호출 후 북마크 저장
  saveBookmarkBtn.addEventListener('click', function() {
    chrome.tabs.query({ active: true, currentWindow: true }, function(tabs) {
      const currentTab = tabs[0];
      const bookmark = {
        title: currentTab.title,
        url: currentTab.url
      };

      // GPT API 호출하여 태그 생성
      fetchGPTTags(bookmark.url, bookmark.title)
        .then((tags) => {
          bookmark.tags = tags.map(tag => tag.trim()); // GPT에서 생성된 태그를 깔끔하게 정리

          // 기존 북마크 불러오기 및 새로운 북마크 저장
          chrome.storage.sync.get({ bookmarks: [] }, function(data) {
            const bookmarks = data.bookmarks;
            bookmarks.push(bookmark);

               // 백엔드로 북마크 데이터 전송
          sendBookmarkToBackend(bookmark)
          .then((response) => {
            console.log('백엔드로 북마크 저장 완료:', response);

            chrome.storage.sync.set({ bookmarks: bookmarks }, function() {
              displayTags(); // 태그 목록 업데이트
              displayBookmarks(bookmarks); // 북마크 목록 업데이트
            });
          })
          .catch((error) => {
              console.error('백엔드 저장 중 오류 발생:', error);
          });
          });
        })
        .catch((error) => {
          console.error('GPT API 호출 중 오류 발생:', error);
        });
    });
  });

  // 백엔드로 북마크 데이터를 보내는 함수
function sendBookmarkToBackend(bookmark) {
  const backendUrl = "http://localhost:8080/api/tagbookmark/in"; // 백엔드 API URL

  const userName = "test1";
  const email = "test1@gmail.com";
 //사용자 이메일은 계속 보내는 방식으로 했어요

// RequestFrontDto 형식에 맞게 데이터 구성
const requestFrontDto = {
  title: bookmark.title,
  url: bookmark.url,
  tags: bookmark.tags,
  userName: userName,  
  email: email  
};
// 요청 전 로그 출력
console.log('백엔드로 보낼 데이터:', requestFrontDto);

return fetch(backendUrl, {
    method: 'POST',
    headers: {
        'Content-Type': 'application/json',
    },
    body: JSON.stringify(requestFrontDto),
})
.then(response => {
    // 응답 상태 코드 확인
    console.log('응답 상태 코드:', response.status);
    if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
    }
    return response.json();
})
.then(data => {
    console.log('백엔드로부터 응답 받음:', data);
    return data;
})
.catch(error => {
    console.error('백엔드로 데이터 전송 중 오류 발생:', error);
});
}

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

      bookmarks.forEach(bookmark => {
        bookmark.tags.forEach(tag => {
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

  // 초기화 시 태그 목록과 북마크 목록 표시
  displayTags();
  chrome.storage.sync.get({ bookmarks: [] }, function(data) {
    displayBookmarks(data.bookmarks);
  });
});
