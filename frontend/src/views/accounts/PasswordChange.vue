<template>
  <div class="background">
    <div class="container p-3 mt-5 bg-light-ivory formatting">
      <h3>비밀번호 변경하기</h3>
      <div class="input-with-label">
        <input 
          v-model="passwordChangeData.password" 
          v-bind:class="{error : error.password, complete:!error.password&&passwordChangeData.password.length!==0}"
          class="inputs"
          id="password" 
          type="password"
          placeholder="새 비밀번호를 입력해주세요." 
          required
        />
        <label for="password"></label>
        <div class="error-text ml-3" v-if="error.password">{{error.password}}</div>
      </div>

      <div class="input-with-label">
        <input
          v-model="passwordChangeData.passwordConfirm"
          type="password"
          id="password-confirm"
          v-bind:class="{error : error.passwordConfirm, complete:!error.passwordConfirm&&passwordChangeData.passwordConfirm.length!==0}"
          placeholder="비밀번호를 다시 입력해주세요."
          class="inputs"
          @click="isSubmit && clickChangePassword(passwordChangeData)"
          required
        />
        <label for="password-confirm"></label>
        <div class="error-text ml-3" v-if="error.passwordConfirm">{{error.passwordConfirm}}</div>
      </div>
      <div class="buttons mt-4 mb-3">
        <button class="btn change-button" :class="{disabled: !isSubmit}" @click="isSubmit && clickChangePassword(passwordChangeData)" >비밀번호 변경하기</button>
      </div>
    </div>
  </div>
</template>

<script>
// import { mapActions } from 'vuex'
import SERVER from '@/api/api'
import axios from 'axios'
import router from '@/router'
import Swal from 'sweetalert2'

export default {
  name: 'PasswordChange',
  data() {
    return {
      passwordChangeData: {
        jwt: this.$route.query.jwt,
        password: "",
        passwordConfirm: "",
      },
      error: {
        password: false,
        passwordConfirm: false,
      },
      isSubmit: false,
    };
  },
  created() {
    this.component = this;
  },
  watch: {
    passwordChangeData: {
      deep: true,

      handler() {
        this.checkPasswordForm();
        this.checkPasswordConfirmationForm();
      }
    }
  },
  methods: {
    checkPasswordForm() {
      if (this.passwordChangeData.password.length > 0 && this.passwordChangeData.password.length < 8) {
          this.error.password = "비밀번호가 너무 짧아요"
        } else if ( this.passwordChangeData.password.length >= 8 && !this.validPassword(this.passwordChangeData.password) ) {
          this.error.password = "영문, 숫자 포함 8 자리 이상이어야 해요.";
        } else this.error.password = false;
    },
    validPassword(password) {
      var va = /^(?=.*\d)(?=.*[a-z])[0-9a-zA-Z]{8,}$/;
      return va.test(password);
    },
    checkPasswordConfirmationForm() {
      if (this.passwordChangeData.password.length >=8 && this.validPassword(this.passwordChangeData.password)){
        if (this.passwordChangeData.password !== this.passwordChangeData.passwordConfirm )
          this.error.passwordConfirm = "비밀번호가 일치하지 않아요."
        else this.error.passwordConfirm = false;
      }

      // 버튼 활성화 코드 (다 통과 했을 때 넘어갈 수 있도록 생성 )
      let isSubmit = true;
      Object.values(this.error).map(v => {
        if (v) isSubmit = false;
      });
      this.isSubmit = isSubmit;
    },
    // ...mapActions('accountStore', ['clickChangePassword']),

    
    clickChangePassword(newData) {
      if ( this.isSubmit ){
        axios.post(SERVER.URL + SERVER.ROUTES.changepassword, newData)
        .then (() => {
          const Toast = Swal.mixin({
            toast: true,
            position: 'top-end',
            showConfirmButton: false,
            timer: 2000,
            timerProgressBar: true,
            onOpen: (toast) => {
              toast.addEventListener('mouseenter', Swal.stopTimer)
              toast.addEventListener('mouseleave', Swal.resumeTimer)
              }
           })
           Toast.fire({
            icon: 'success',
            title: "비밀번호 변경에 성공하였습니다."
          })
          router.push({ name: 'PasswordChangeSuccessful' })
        })
        .catch (err => {
          console.log(err.response)
        })
      }
      
      
    },
  }
}
</script>

<style scoped>
.container {
  width: 30%;
  border-radius: 25px;
}

h3 { 
  color: #88A498;
}

.inputs {
  border-style: none;
  border-bottom: 1px solid #88A498;
  background-color: transparent;
  width: 100%;
  padding: 10px;
  padding-left: 20px;
  margin-top: 20px;
}

.change-button{
  background-color: #88A498;
  color: #F8F8F8;
  width: 70%;
  /* font-size: 10px; */
}

.change-button:hover {
  background-color: #3c755a;
  color: #F8F8F8;
}

.inputs:focus {
  border-style: none;
  border-bottom: 2px solid #D6CBBD;
  outline-style: none;
}

input[type="password"] {
  font-family:sans-serif;
}
.error, .error:focus {
  border-bottom: 2px solid rgb(250, 25, 59, 0.7); 
}

.error-text {
  color: rgb(250, 25, 59, 0.7);
  text-align: left;
  padding-left: 5px;
}

.disabled, .disabled:hover {
  background-color: rgb(136, 154, 152, 0.25);
  color: #F8F8F8;
}

.background {
  background-image: url("https://user-images.githubusercontent.com/25967949/90751489-27ce4480-e311-11ea-93aa-2ab9d1f41b4e.png");
  position: absolute;
  top: 0;
  left: 0;
  bottom: 0;
  right: 0;
  background-repeat: repeat;
}


.formatting {
  margin-top: 20vh !important;
  opacity: 0.9;
}
</style>