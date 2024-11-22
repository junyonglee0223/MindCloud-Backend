// 팝업 오버레이 관리 로직
document.addEventListener('DOMContentLoaded', function () {
    const popupOverlay = document.getElementById('popupOverlay');
    const bookmarkNameInput = document.getElementById('bookmarkName');
    const bookmarkUrlInput = document.getElementById('bookmarkUrl');
    const tagList = document.getElementById('tagList');
    const addTagInput = document.getElementById('addTagInput');
    const addTagBtn = document.getElementById('addTagBtn');
    const saveBtn = document.getElementById('saveBtn');
    const cancelBtn = document.getElementById('cancelBtn');
  
    let currentBookmark = null; // 현재 편집 중인 북마크 데이터
    console.log('Popup overlay element:', popupOverlay);
    console.log("Initial popupOverlay classList:", popupOverlay.classList);
    //test!!!!!!!!!!!!!!!!!!!!!!!!!

    popupOverlay.classList.add('hidden'); // 항상 숨겨진 상태로 시작
    
    // 팝업 열기 함수
    window.openEditPopup = function (bookmark) {
        const editPopup = document.getElementById('editPopup');
        editPopup.style.width = '500px';
        editPopup.style.height = '400px';


        console.log("openEditPopup called with:", bookmark);//test!!!!!!!!!!!!!!!!!!
        if (!bookmark) {
            console.error('Bookmark data is missing!');
            return;
        }
        currentBookmark = bookmark;
    
        // 기존 데이터 채우기
        bookmarkNameInput.value = currentBookmark.title;
        bookmarkUrlInput.value = currentBookmark.url;
        renderTags(currentBookmark.tags);
    
        // 팝업창 열기
        popupOverlay.classList.remove('hidden');
    };
  
    // 태그 렌더링 함수
    function renderTags(tags) {
      tagList.innerHTML = ''; // 기존 태그 초기화
      tags.forEach((tag, index) => {
        const tagElement = document.createElement('div');
        tagElement.className = 'tag';
        tagElement.innerHTML = `${tag} <span class="remove" data-index="${index}">x</span>`;
        tagList.appendChild(tagElement);
      });
    }
  
    // 태그 제거 이벤트
    tagList.addEventListener('click', function (event) {
      if (event.target.classList.contains('remove')) {
        const index = event.target.dataset.index;
        currentBookmark.tags.splice(index, 1); // 해당 태그 제거
        renderTags(currentBookmark.tags); // 업데이트된 태그 렌더링
      }
    });
  
    // 태그 추가 버튼 클릭 이벤트
    addTagBtn.addEventListener('click', function () {
      const newTag = addTagInput.value.trim();
      if (newTag && !currentBookmark.tags.includes(newTag)) {
        currentBookmark.tags.push(newTag);
        renderTags(currentBookmark.tags);
        addTagInput.value = ''; // 입력창 초기화
      }
    });
  
    // 저장 버튼 클릭 이벤트
    saveBtn.addEventListener('click', function () {
      currentBookmark.title = bookmarkNameInput.value.trim();
      currentBookmark.url = bookmarkUrlInput.value.trim();
  
      // 1. 백엔드에 데이터 저장
      sendBookmarkToBackend(currentBookmark)
        .then(() => {
          // 2. chrome.storage.sync에 데이터 저장
          chrome.storage.sync.get({ bookmarks: [] }, function (data) {
            // 중복 URL 확인
            const updatedBookmarks = data.bookmarks.filter(
              (bookmark) => bookmark.url !== currentBookmark.url
            );
            updatedBookmarks.push(currentBookmark);
            chrome.storage.sync.set({ bookmarks: updatedBookmarks }, function () {
              alert('북마크가 저장되었습니다.');
              // 3. 동기화 후 화면 업데이트
              initializeData();
              
              popupOverlay.classList.add('hidden'); // 팝업창 닫기
              console.log('6. 팝업 닫기 완료');//test!!!!!!!!!!!!!!!!!!
            });
          });
        })
        .catch((error) => {
          console.error('북마크 저장 중 오류 발생:', error);
          alert('북마크 저장에 실패했습니다.');
        });
    });
  
    // 취소 버튼 클릭 이벤트
    cancelBtn.addEventListener('click', function () {
      popupOverlay.classList.add('hidden'); // 팝업창 닫기
    });
  });
  