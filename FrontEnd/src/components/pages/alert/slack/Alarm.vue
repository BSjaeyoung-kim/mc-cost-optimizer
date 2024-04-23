<template>
  <section
    id="alert-set-table"
    class="-wrapper-column -wrapper-tile w-100 ">
    <div class="alert-set">
      <div class="btn-space">
        <b-button
          :disabled="toggleSaveBtn"
          :variant="'green-2'"
          class="guide-btn"
          @click="openGuide"
        >
          가이드 열기
        </b-button>
        <b-button
          :disabled="toggleSaveBtn"
          :variant="'blue-1'"
          class="test-btn"
          @click="alarmSend"
        >
          Alarm Test
        </b-button>
      </div>

      <table>
        <tr>
          <th>
            Slack App Token
          </th>
          <td style="display: flex;">
            <b-input
              :type="inputTkType"
              v-model="token"
              class="save-token-input"
              @focus="showTextTk"
              @blur="hideTextTk"
            />
          </td>
        </tr>
        <tr>
          <th>
            Channel ID
          </th>
          <td style="display: flex;">
            <b-input
              :type="inputChType"
              v-model="channel"
              class="save-token-input"
              @focus="showTextCh"
              @blur="hideTextCh"
            />
          </td>
        </tr>
        <tr>
          <th>
            Alarm Message
          </th>
          <td style="display: flex;">
            <b-input
              v-model="testMsg"
              class="save-token-input"
            />
          </td>
        </tr>
      </table>
      <div class="alert-footer">
        <b-button
          :disabled="toggleSaveBtn"
          :variant="'blue-2'"
          class="save-token-btn"
          @click="saveToken"
        >
          저장
        </b-button>
      </div>
    </div>
    <div class="chatbot-guide-modal">
      <b-modal
        id="bv-modal-ch-guide"
        ref="bv-modal-ch-guide"
        size="xl"
        hide-footer
        @hidden="closeGuide">
        <template #modal-title>
          챗봇 & 토큰 생성 가이드
        </template>
        <div style="display: flex; color: #666; margin-left: 10px;">
          <ImageSlider
            :items="imgItem"
          />
        </div>
      </b-modal>
    </div>
  </section>
</template>

<script>
import axios from "axios";
import ImageSlider from "@/components/pages/alert/ImageSlider";

export default {
  name: "Alarm",
  components: {
    ImageSlider
  },
  data() {
    let imgItem = [];
    for (let i = 1; i <= 17; i++) {
      imgItem.push({ src: require(`@/assets/images/slack-guide-${i}.png`) });
    }
    return {
      token: "",
      channel: "",
      toggleSaveBtn: false,
      inputTkType: "password",
      inputChType: "password",
      linkUrl: null,
      linkText: null,
      imgItem,
      testMsg: "",
    }
  },
  methods: {
    saveToken() {
      this.toggleSaveBtn = true
      let userId = '2194155'
      let token = this.token
      let channel = this.channel
      const params = {
        id: userId,
        token: token,
        channel: channel
      }
      axios.post('http://localhost:9000/insertToken', params)
        .then(() => {
          alert('Save.')
          this.toggleSaveBtn = false
        })
        .catch((err) =>{
          console.log(err)
          alert('Error.')
          this.toggleSaveBtn = false
        })
    },
    alarmSend() {
      this.toggleSaveBtn = true
      let userId = '2194155'
      // let message = "이상 비용이 발생하였습니다. 자세한 내용은 우측 링크를 클릭해주세요.(테스트 메세지)"
      // let linkUrl = 'http://localhost:8080/dashboard'
      // let linkText = '이동하기'
      const params = new URLSearchParams({
        userId: userId,
        message: this.testMsg
      });
      if (!!this.linkUrl) {
        params.append('linkUrl', this.linkUrl);
      }
      if (!!this.linkText) {
        params.append('linkText', this.linkText);
      }
      axios.post('http://localhost:9000/sendAC', params)
        .then((res) => {
          if (this.testMsg === "" || this.testMsg === null) {
            alert("메시지를 입력해주세요.")
          } else {
            alert(res.data)
          }
          this.toggleSaveBtn = false
        })
        .catch((err) =>{
          console.log(err)
          alert('오류가 발생하여 전송에 실패했습니다.')
          this.toggleSaveBtn = false
        })
    },
    showTextTk() {
      this.inputTkType = 'text';
    },
    showTextCh() {
      this.inputChType = 'text';
    },
    hideTextTk() {
      this.inputTkType = 'password';
    },
    hideTextCh() {
      this.inputChType = 'password';
    },
    openGuide() {
      this.$refs['bv-modal-ch-guide'].show()
    },
    closeGuide() {
      this.$refs['bv-modal-ch-guide'].hide()
    },
    // openTest() {
    //   this.$refs['bv-modal-alm-test'].show()
    // },
    // closeTest() {
    //   this.testMsg = ""
    //   this.$refs['bv-modal-alm-test'].hide()
    // }
  }
}
</script>

<style lang="scss">
#alert-set-table {
  .alert-set {
    margin: 25px 0 5px 30px;
    table {
      border-collapse: collapse;
      width: 100%;
    }

    th, td {
      border: 1px solid #dddddd;
      text-align: left;
      padding: 8px;
      height: 50px;
    }

    th {
      background-color: #f2f2f2;
      width: 200px;
      padding-left: 30px;
      font-size: 0.8rem;
    }

    .save-token-input {
      width: 210px;
      font-size: 12px;
      background-color: white;
      text-overflow: ellipsis;
      overflow: hidden;
      white-space: nowrap;
    }
    .btn-space {
      width: 100%;
      display: flex;
      margin-bottom: 20px;
    }

    .test-btn {
      font-size: 0.8rem;
      margin-left: 20px;
      text-align: center;
      color:#FFF;
    }

    .guide-btn {
      font-size: 0.8rem;
      text-align: center;
      color: #FFF;
    }

    .alert-footer {
      margin: 15px 10px;
      width: 100%;
      display: flex;
    }

    .save-token-btn {
      margin: 10px 10px 0 auto;
    }
    .alert-guide {
      margin-top: 25px;
    }
  }
  //#bv-modal-ch-guide___BV_modal_body_ {
  //  .modal-title {
  //    font-size: large;
  //    font-weight: bold;
  //  }
  //  .modal-body {
  //    height: 750px;
  //    overflow-y: auto;
  //  }
  //}
}
.modal-title {
  font-size: 24px;
  font-weight: bold;
}
</style>
