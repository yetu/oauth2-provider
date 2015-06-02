package com.yetu.oauth2provider.data.riak

import com.scalapenos.riak.{ RiakBucket, RiakClient }

trait RiakConnection {

  def port: Int
  def host: String
  def accessTokenBucketName: String
  def authCodeBucketName: String
  def mailTokenBucketName: String

  def riakClient: RiakClient = RiakClient(host, port)
  def accessTokenBucket: RiakBucket = riakClient.bucket(accessTokenBucketName)
  def authCodeBucket: RiakBucket = riakClient.bucket(authCodeBucketName)
  def mailTokenBucket: RiakBucket = riakClient.bucket(mailTokenBucketName)
}

