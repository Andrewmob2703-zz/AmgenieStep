// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.


function openMemePage() {
  if (confirm("Some of the words in the memes may be inappropriate for some viewers. Continue?")) {
    window.open("meme_view.html",'targetWindow','toolbar=no,location=no,status=no,menubar=no,scrollbars=yes,resizable=yes,width=800,height=500'); return false;
  }
  else{
    alert("Memes cancelled!");
  }
}

function randomMeme() {    
  // Have 11 images in my directory, so generate a random index between
  // 1 and 11.
  const memeIndex = Math.floor(Math.random() * 11) + 1;
  const memeUrl = '/Genie_Imgs/memes/meme' + memeIndex + '.jpg';

  const memeElement = document.createElement('img');
  memeElement.src = memeUrl;

  const memeContainer = document.getElementById('random-image-container');
  // Remove the previous image.
  memeContainer.innerHTML = '';
  memeContainer.appendChild(memeElement);
}

function showHiddenPanel() {
  document.getElementById("panel").style.display = "block";
}

function openProjects(){
  window.open("projects.html", "_self")
}

function openGallery(){
  window.open("G_gallery.html", "_self")
}

function openContact(){
  window.open("contact_me.html")
}

function validateForm() {
  var name = document.forms["commentForm"]["name"].value;
  var comments = document.forms["commentForm"]["comment"].value;
  if (name === "" || comments === "") {
    alert("Name and comments cannot be empty.");
    return false;
  }
}

function loadComments() {
  fetch('/data').then(response => response.json()).then((messages) => {
    const commentListElement = document.getElementById('comment-list');
    messages.forEach((message) => {
      commentListElement.appendChild(createCommentElement(message));
    })
  });
}

function createCommentElement(message) {
  const commentElement = document.createElement('li');
  commentElement.className = 'comment';

  const messageElement = document.createElement('span');
  messageElement.innerText = message.comment;

  commentElement.appendChild(messageElement);

  return commentElement;
}
